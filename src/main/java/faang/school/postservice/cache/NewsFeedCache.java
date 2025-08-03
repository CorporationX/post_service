package faang.school.postservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
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
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsFeedCache implements RedisCache {

    private static final String USER_CACHE_KEY_PREFIX = "user_cache:";
    private static final String POST_CACHE_KEY_PREFIX = "post_cache:";
    private static final String FEED_CACHE_KEY_PREFIX = "feed_cache:";
    private static final String POST_COMMENTS_KEY_PREFIX = "comments_cache:";
    private static final String POST_VIEWS_KEY_PREFIX = "post_views:";

    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisNewsFeedTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis-news-feed.size}")
    private int feedSizeLimit;
    @Value("${spring.data.redis-news-feed.user-ttl}")
    private int userTTL;
    @Value("${spring.data.redis-news-feed.comments-limit}")
    private int commentsLimit;

    private DefaultRedisScript<Long> addAndTrimScript;
    private DefaultRedisScript<Long> addCommentAndTrimScript;

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
    }

    @Override
    public void putUser(Long user) {
        String key = USER_CACHE_KEY_PREFIX + user;
        redisNewsFeedTemplate.opsForValue().set(key, user, Duration.ofMinutes(5));
//        redisNewsFeedTemplate.opsForValue().set(key, user, Duration.ofDays(userTTL)); // не забыть раскоментить после тестов.
        log.info("Cached user with key: {}", key);
    }

    @Override
    public void putPost(RedisPostDto post) {
        String key = POST_CACHE_KEY_PREFIX + post.getId();
        redisNewsFeedTemplate.opsForValue().set(key, post);
//        log.info("Cached post with key: {}", key);
    }

    @Override
    public void putFeed(Long userId, Long postId, LocalDateTime postCreatedAt) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        redisNewsFeedTemplate.opsForZSet().add(key, postId, System.currentTimeMillis());
        log.info("Added post {} to user {} feed with score {}", postId, userId, postCreatedAt);
    }

    @Override
    public RedisUserDto getUser(Long userId) {
        String key = USER_CACHE_KEY_PREFIX + userId;
        return (RedisUserDto) redisNewsFeedTemplate.opsForValue().get(key);
    }

    @Override
    public RedisPostDto getPost(Long postId) {
        String key = POST_CACHE_KEY_PREFIX + postId;
        RedisPostDto post = (RedisPostDto) redisNewsFeedTemplate.opsForValue().get(key);
        if (post != null) {
            post.setCommentCount(getPostViews(postId));
        }
        return post;
    }

    @Override
    public Set<RedisPostDto> getFeed(Long userId, long start, long end) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        Set<Object> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, start, end);
        if (postIds == null) {
            return Collections.emptySet();
        }

        return postIds.stream()
                .map(id -> getPost((Long) id))
                .collect(Collectors.toSet());
    }

    @Async("redisTaskExecutor")
    @Override
    public void putFeedForSubscribers(Long user, List<Long> followerIds) {
        Pageable pageable = PageRequest.of(0, feedSizeLimit);
        List<RedisPostDto> posts = postRepository.findLatestPostsByAuthorId(user, pageable);
        log.info("found {} Posts for user with ID {}", posts.size(), user);

        if (posts.isEmpty()) {
            log.info("No posts found for user {}. Skipping feed update for followers.", user);
            return;
        }
        putUser(user);

        posts.forEach(this::putPost);

        List<Object> scriptArgs = posts.stream()
                .flatMap(post -> {
                    double score = post.getCreatedAt().toEpochSecond(ZoneOffset.UTC) * 1000.0;
                    return Stream.of(score, (Object) post.getId());
                })
                .collect(Collectors.toList());

        for (Long followerId : followerIds) {
            putFeedBatch(followerId, scriptArgs);
        }
    }

    @Override
    public void updatePostComment(long postId) {
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
    public void addCommentToPostCache(KafkaCommentEventDto dto) {
        String key = POST_COMMENTS_KEY_PREFIX + dto.postId();
        long score = dto.createdAt().toEpochSecond(ZoneOffset.UTC);

        String serializedComment = null;
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

        updatePostComment(dto.postId());
    }

    @Override
    public void updatePostViews(long postId) {
        String key = POST_VIEWS_KEY_PREFIX + postId;
        redisNewsFeedTemplate.opsForValue().increment(key);
    }

    public Long getPostViews(long postId) {
        String key = POST_VIEWS_KEY_PREFIX + postId;
        return (Long) redisNewsFeedTemplate.opsForValue().get(key);
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