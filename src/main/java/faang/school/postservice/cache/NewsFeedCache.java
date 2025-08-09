package faang.school.postservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsFeedCache implements RedisCache {

    private static final String USER_CACHE_KEY_PREFIX = "user_cache:";
    private static final String POST_CACHE_KEY_PREFIX = "post_cache:";
    private static final String FEED_CACHE_KEY_PREFIX = "feed_cache:";
    private static final String POST_COMMENTS_KEY_PREFIX = "comments_cache:";

    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisNewsFeedTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis-news-feed.size}")
    private int feedSizeLimit;
    @Value("${spring.data.redis-news-feed.user-ttl}")
    private int userTTL;
    @Value("${spring.data.redis-news-feed.comments-limit}")
    private int commentsLimit;
    @Value("${spring.data.redis-news-feed.post-ttl}")
    private int postTTL;

    private DefaultRedisScript<Long> addAndTrimScript;
    private DefaultRedisScript<Long> addCommentAndTrimScript;
    private DefaultRedisScript<Long> incrementPostCounterScript;
    private DefaultRedisScript<Long> batchFeedUpdateScript;
    private DefaultRedisScript<Long> batchMultiPostFeedUpdateScript;
    private DefaultRedisScript<Long> batchSetAndExpireScript;

    @PostConstruct
    public void init() {
        addAndTrimScript = new DefaultRedisScript<>();
        addAndTrimScript.setScriptText("""
                    redis.call('ZADD', KEYS[1], unpack(ARGV, 2))
                    redis.call('ZREMRANGEBYRANK', KEYS[1], 0, -tonumber(ARGV[1] + 1))
                    return redis.call('ZCARD', KEYS[1])
                """);
        addAndTrimScript.setResultType(Long.class);

        // Скрипт для комментариев к посту
        addCommentAndTrimScript = new DefaultRedisScript<>();
        addCommentAndTrimScript.setScriptText("""
                     local key = KEYS[1]
                     local limit = tonumber(ARGV[1])
                     local score = tonumber(ARGV[2])
                     local member = ARGV[3]
                     -- ZADD идемпотентен.
                     redis.call('ZADD', key, score, member)
                     -- ZREMRANGEBYRANK 0, -limit-1 удаляет все элементы, кроме последних 'limit'.
                     redis.call('ZREMRANGEBYRANK', key, 0, -limit-1)
                     return redis.call('ZCARD', key)
                """);
        addCommentAndTrimScript.setResultType(Long.class);
        //incrementPostCounterScript,
        //Collections.singletonList(postKey),
        //event,
        //String.valueOf(currentVersion)
        incrementPostCounterScript = new DefaultRedisScript<>();
        incrementPostCounterScript.setScriptText("""
                    -- KEYS[1]: Ключ для RedisPostDto (post_cache:{postId})
                    -- ARGV[1]: Тип счетчика для инкремента ("likes", "comments", "views")
                    -- ARGV[2]: Ожидаемая версия (для оптимистической блокировки)
                
                    local postKey = KEYS[1]
                    local incrementType = ARGV[1]
                    local expectedVersion = tonumber(ARGV[2])
                
                    local postJson = redis.call('GET', postKey)
                
                    if not postJson then
                        -- Пост не найден в кэше, не можем обновить.
                        return 0
                    end
                
                    local post = cjson.decode(postJson)
                
                    -- Проверка оптимистической блокировки
                    if post.version ~= expectedVersion then
                        -- Версия не совпадает, произошла конкурентная модификация.
                        return 0
                    end
                
                    -- Инкремент соответствующего счетчика
                    if incrementType == 'like_event' then
                        post.likeCount = (post.likeCount or 0) + 1
                    elseif incrementType == 'comment_event' then
                        post.commentCount = (post.commentCount or 0) + 1
                    elseif incrementType == 'post_viewed_event' then
                        post.viewsCount = (post.viewsCount or 0) + 1
                    else
                        -- Неизвестный тип инкремента
                        return 0
                    end
                
                    -- Инкремент версии
                    post.version = post.version + 1
                
                    -- Сериализуем обратно в JSON и сохраняем
                    local updatedPostJson = cjson.encode(post)
                    redis.call('SET', postKey, updatedPostJson)
                
                    return 1 -- Успешное обновление
                """);
        incrementPostCounterScript.setResultType(Long.class);

        batchFeedUpdateScript = new DefaultRedisScript<>();
        batchFeedUpdateScript.setScriptText("""
                local postId = KEYS[1]
                local score = tonumber(KEYS[2])
                local ttl = tonumber(KEYS[3])
                local feedSizeLimit = tonumber(KEYS[4])
                local feedKeyPrefix = 'feed_cache:'
                
                -- Итерируемся по каждому userId в ARGV
                for i = 1, #ARGV, 1 do
                    local userId = ARGV[i]
                    local feedKey = feedKeyPrefix .. userId
                
                    -- Добавляем пост в отсортированное множество
                    redis.call('ZADD', feedKey, score, postId)
                
                    -- Обрезаем множество, оставляя только feedSizeLimit самых новых постов
                    redis.call('ZREMRANGEBYRANK', feedKey, 0, -feedSizeLimit - 1)
                
                    -- Устанавливаем срок жизни для ключа фида
                    redis.call('EXPIRE', feedKey, ttl)
                end
                
                return 1
                """);
        batchFeedUpdateScript.setResultType(Long.class);

        batchMultiPostFeedUpdateScript = new DefaultRedisScript<>();
        batchMultiPostFeedUpdateScript.setScriptText("""
                local ttl = tonumber(KEYS[1])
                local feedSizeLimit = tonumber(KEYS[2])
                local feedKeyPrefix = 'feed_cache:'
                
                local userIds = {}
                local postArgs = {}
                local isPostArgs = false
                
                for i = 1, #ARGV do
                    if ARGV[i] == 'POSTS_START' then
                        isPostArgs = true
                    elseif not isPostArgs then
                        table.insert(userIds, ARGV[i])
                    else
                        table.insert(postArgs, ARGV[i])
                    end
                end
                
                for i = 1, #userIds do
                    local userId = userIds[i]
                    local feedKey = feedKeyPrefix .. userId
                
                    redis.call('ZADD', feedKey, unpack(postArgs))
                
                    redis.call('ZREMRANGEBYRANK', feedKey, 0, -feedSizeLimit - 1)
                
                    redis.call('EXPIRE', feedKey, ttl)
                end
                
                return 1
                """);
        batchMultiPostFeedUpdateScript.setResultType(Long.class);

        batchSetAndExpireScript = new DefaultRedisScript<>();
        batchSetAndExpireScript.setScriptText("""
            local ttl = tonumber(ARGV[1])
            for i = 1, #KEYS do
                redis.call('SET', KEYS[i], ARGV[i+1])
                redis.call('EXPIRE', KEYS[i], ttl)
            end
            return #KEYS
        """);
        batchSetAndExpireScript.setResultType(Long.class);
    }

    // change to get save User
    @Override
    public void putUser(UserFeedDto user) {
        try {
            String key = USER_CACHE_KEY_PREFIX + user.userId();
            redisNewsFeedTemplate.opsForValue().set(key, user, Duration.ofDays(userTTL));
            log.info("User {} cached successfully", user.userId());
        } catch (Exception e) {
            log.error("Failed to cache user {}: {}", user.userId(), e.getMessage());
            throw new RuntimeException("Failed to cache user", e); // Custom exception
        }
    }

    @Override
    public void putPost(RedisPostDto post) {
        try {
            String key = POST_CACHE_KEY_PREFIX + post.getPostId();
            redisNewsFeedTemplate.opsForValue().set(key, post, Duration.ofSeconds(postTTL));
            log.info("Post {} cached successfully", post.getPostId());
        } catch (Exception e) {
            log.error("Failed to cache post {}: {}", post.getPostId(), e.getMessage());
            throw new RuntimeException("Failed to cache post", e); // Custom exception
        }
    }

    @Override
    public void putFeed(Long userId, Long postId, LocalDateTime postCreatedAt) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        long score = postCreatedAt.toEpochSecond(ZoneOffset.UTC);
        redisNewsFeedTemplate.execute(
                addAndTrimScript,
                Collections.singletonList(key),
                feedSizeLimit, score, postId
        );
        log.info("Added post {} to user {} feed with score {}", postId, userId, postCreatedAt);
    }

    @Override
    public void putFeedForUserBatch(List<Long> userIds, Long postId, LocalDateTime postCreatedAt) {
        if (userIds.isEmpty()) {
            return;
        }

        long score = postCreatedAt.toEpochSecond(ZoneOffset.UTC);
        long ttlInSeconds = Duration.ofMinutes(userTTL).getSeconds();

        List<Object> scriptArgs = userIds.stream().map(Object.class::cast).toList();

        redisNewsFeedTemplate.execute(
                batchFeedUpdateScript,
                List.of(String.valueOf(postId), String.valueOf(score), String.valueOf(ttlInSeconds), String.valueOf(feedSizeLimit)),
                scriptArgs.toArray()
        );
        log.info("Added post {} to {} users' feeds in a batch.", postId, userIds.size());
    }

    @Override
    public RedisUserDto getUser(Long userId) {
        String key = USER_CACHE_KEY_PREFIX + userId;
        return (RedisUserDto) redisNewsFeedTemplate.opsForValue().get(key);
    }

    @Override
    public RedisPostDto getPost(Long postId) {
        String key = POST_CACHE_KEY_PREFIX + postId;
        return (RedisPostDto) redisNewsFeedTemplate.opsForValue().get(key);
    }

    @Override
    public List<RedisPostDto> getPostsBatch(Set<Long> postIds) {
        List<String> keys = postIds.stream()
                .map(id -> POST_CACHE_KEY_PREFIX + id)
                .collect(Collectors.toList());
        return redisNewsFeedTemplate.opsForValue().multiGet(keys)
                .stream()
                .filter(obj -> obj instanceof RedisPostDto)
                .map(obj -> (RedisPostDto) obj)
                .collect(Collectors.toList());
    }

    @Override
    public List<Long> getComments(Long postId) {
        return List.of();
    }

    @Override
    public boolean updatePost(long postId, String event) {
        RedisPostDto postToUpdate = getPost(postId);
        if (postToUpdate == null) {
            // Оптимизация 3: Fallback. Если пост не найден в кэше, загружаем его из БД
            postToUpdate = postRepository.findPostForRedisByPostId(postId) // Предположим, есть маппер из Post в RedisPostDto
                    .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist in cache or db"));

            if (postToUpdate == null) {
                log.warn("Post with ID {} not found in DB. Cannot update counter for event: {}.", postId, event);
                return false;
            }

            // Добавляем свежий пост в кэш
            putPost(postToUpdate);
        }

        String postKey = POST_CACHE_KEY_PREFIX + postId;
        long currentVersion = postToUpdate.getVersion();

        Long result = redisNewsFeedTemplate.execute(
                incrementPostCounterScript,
                Collections.singletonList(postKey),
                event,
                String.valueOf(currentVersion)
        );

        if (result != null && result == 1L) {
            log.info("Successfully updated post {} counter for event {}. Old version: {}", postId, event, currentVersion);
            return true;
        } else {
            log.warn("Failed to update post {} counter for event {}. Optimistic locking failed or post not found. Old version: {}", postId, event, currentVersion);
            return false;
        }
    }

    @Override
    public Set<Long> getFeed(Long userId, long start, long end) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        Set<Object> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, start, end);
        if (postIds == null) {
            return Collections.emptySet();
        }

        return postIds.stream()
                .map(id -> (Long) id)
                .collect(Collectors.toSet());
    }

    @Override
    public void putFeedForSubscribers(Long userId, List<Long> followerIds) {
        Pageable pageable = PageRequest.of(0, feedSizeLimit);
        List<RedisPostDto> posts = postRepository.findLatestPostsByAuthorId(userId, pageable);
        log.info("found {} Posts for user with ID {}", posts.size(), userId);

        if (posts.isEmpty()) {
            log.info("No posts found for user {}. Skipping feed update for followers.", userId);
            return;
        }

        putPostsBatch(posts);

        List<Object> scriptArgs = new ArrayList<>();
        scriptArgs.addAll(followerIds.stream().map(Object.class::cast).toList());
        scriptArgs.add("POSTS_START");

        posts.forEach(post -> {
            scriptArgs.add((double) post.getCreatedAt().toEpochSecond(ZoneOffset.UTC));
            scriptArgs.add(post.getPostId());
        });

        long ttlInSeconds = Duration.ofMinutes(userTTL).getSeconds();

        redisNewsFeedTemplate.execute(
                batchMultiPostFeedUpdateScript,
                List.of(String.valueOf(ttlInSeconds), String.valueOf(feedSizeLimit)),
                scriptArgs.toArray()
        );
        log.info("Added {} posts from author {} to {} followers' feeds.", posts.size(), userId, followerIds.size());
    }

    @Override
    public void putPostsBatch(List<RedisPostDto> posts) {
        if (posts.isEmpty()) {
            return;
        }

        List<String> keys = posts.stream()
                .map(post -> POST_CACHE_KEY_PREFIX + post.getPostId())
                .collect(Collectors.toList());

        List<String> serializedPosts = posts.stream()
                .map(post -> {
                    try {
                        return objectMapper.writeValueAsString(post);
                    } catch (JsonProcessingException e) {
                        log.error("Failed to serialize post {}: {}", post.getPostId(), e.getMessage());
                        return null;
                    }
                })
                .toList();

        // Оптимизация 2: используем новый атомарный скрипт
        Object[] args = new Object[serializedPosts.size() + 1];
        args[0] = String.valueOf(postTTL);
        System.arraycopy(serializedPosts.toArray(), 0, args, 1, serializedPosts.size());

        redisNewsFeedTemplate.execute(
                batchSetAndExpireScript,
                keys,
                args
        );

        log.info("Successfully cached {} posts in a batch with atomic TTL setting.", posts.size());
    }

    @Override
    public void updateComment(long postId) {
        RedisPostDto postToUpdate = getPost(postId);

        if (postToUpdate != null) {
            long currentCommentCount = postToUpdate.getCommentCount();
            postToUpdate.setCommentCount(currentCommentCount + 1);
            putPost(postToUpdate);
            log.info("Successfully updated comment count for post {}. New count: {}"
                    , postId, postToUpdate.getCommentCount());
        } else {
            log.warn("Post with ID {} not found in cache. Cannot update comment count.", postId);
        }
    }

    @Async("redisTaskExecutor")
    @Override
    public void putComment(KafkaCommentEventDto dto) {
        String key = POST_COMMENTS_KEY_PREFIX + dto.postId();
        long score = dto.createdAt().toEpochSecond(ZoneOffset.UTC);

        String serializedComment;
        try {
            serializedComment = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize CommentDto: {}", dto, e);
            return;
        }
        List<Object> scriptArgs = new ArrayList<>();
        scriptArgs.add(commentsLimit);
        scriptArgs.add(score);
        scriptArgs.add(serializedComment);

        redisNewsFeedTemplate.execute(
                addCommentAndTrimScript,
                Collections.singletonList(key),
                scriptArgs.toArray()
        );

        updateComment(dto.postId());
    }

    public void putFeedBatch(Long userId, List<Object> scriptArgs) {
        String key = FEED_CACHE_KEY_PREFIX + userId;

        List<Object> allScriptArgs = new ArrayList<>(scriptArgs.size() + 1);
        allScriptArgs.add(feedSizeLimit);
        allScriptArgs.addAll(scriptArgs);

        redisNewsFeedTemplate.execute(
                addAndTrimScript,
                Collections.singletonList(key),
                allScriptArgs.toArray()
        );
    }


}