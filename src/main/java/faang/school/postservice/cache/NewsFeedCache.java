package faang.school.postservice.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.UserFeedDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
    private final CommentRepository commentRepository;
    private final RedisTemplate<String, Object> redisNewsFeedTemplate;
    private final RedisTemplate<String, String> redisNewsFeedStringLuaTemplate;
    private final ObjectMapper objectMapper;
    private final NewsFeedConfiguration newsFeedConfiguration;

    private DefaultRedisScript<Long> addCommentAndTrimScript;
    private DefaultRedisScript<Long> incrementPostCounterScript;
    private DefaultRedisScript<Long> batchFeedUpdateScript;
    private DefaultRedisScript<Long> batchMultiPostFeedUpdateScript;
    private DefaultRedisScript<Long> batchSetAndExpireScript;

    @PostConstruct
    public void init() {
        addCommentAndTrimScript = new DefaultRedisScript<>();
        addCommentAndTrimScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/add-comment-and-trim.lua")));
        addCommentAndTrimScript.setResultType(Long.class);

        incrementPostCounterScript = new DefaultRedisScript<>();
        incrementPostCounterScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/increment-post-counter.lua")));
        incrementPostCounterScript.setResultType(Long.class);

        batchFeedUpdateScript = new DefaultRedisScript<>();
        batchFeedUpdateScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/batch-feed-update.lua")));
        batchFeedUpdateScript.setResultType(Long.class);

        batchMultiPostFeedUpdateScript = new DefaultRedisScript<>();
        batchMultiPostFeedUpdateScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/batch-multi-post-feed-update.lua")));
        batchMultiPostFeedUpdateScript.setResultType(Long.class);

        batchSetAndExpireScript = new DefaultRedisScript<>();
        batchSetAndExpireScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/batch-set-and-expire.lua")));
        batchSetAndExpireScript.setResultType(Long.class);
    }

    @Override
    public void putUser(UserFeedDto user) {
        try {
            String key = USER_CACHE_KEY_PREFIX + user.userId();
            redisNewsFeedTemplate.opsForValue().set(key, user, Duration.ofDays(newsFeedConfiguration.getUserTtl()));
            log.info("User {} cached successfully", user.userId());
        } catch (Exception e) {
            log.error("Failed to cache user {}: {}", user.userId(), e.getMessage());
        }
    }

    @Override
    public void putPost(RedisPostDto post) {
        try {
            String key = POST_CACHE_KEY_PREFIX + post.getPostId();
            // --- MODIFICATION START ---
            // Manually serialize the object to a JSON string using ObjectMapper
            String serializedPost = objectMapper.writeValueAsString(post);
            // Use the redisNewsFeedStringLuaTemplate (configured with StringRedisSerializer)
            // to ensure a consistent, clean string format.
            log.info("PutPost -> serializedPost: {}", serializedPost);
            redisNewsFeedStringLuaTemplate.opsForValue().set(key, serializedPost, Duration.ofMinutes(newsFeedConfiguration.getPostTtl() + 10L).getSeconds());
            // --- MODIFICATION END ---
            log.info("Post {} cached successfully", post.getPostId());
        } catch (Exception e) {
            log.error("Failed to cache post {}: {}", post.getPostId(), e.getMessage());
        }
    }

    @Override
    public void putFeedForUserBatch(List<Long> userIds, Long postId, LocalDateTime postCreatedAt) {
        if (userIds.isEmpty()) {
            return;
        }

        long score = postCreatedAt.toEpochSecond(ZoneOffset.UTC);
        long ttlInSeconds = Duration.ofMinutes(newsFeedConfiguration.getUserTtl()).getSeconds();

        List<Object> scriptArgs = userIds.stream().map(Object.class::cast).toList();

        redisNewsFeedTemplate.execute(
                batchFeedUpdateScript,
                List.of(
                        String.valueOf(postId),
                        String.valueOf(score),
                        String.valueOf(ttlInSeconds),
                        String.valueOf(newsFeedConfiguration.getFeedSize())
                ),
                scriptArgs.toArray()
        );
        log.info("Added post {} to {} users' feeds in a batch.", postId, userIds.size());
    }

    @Override
    public UserFeedDto getUser(Long userId) {
        String key = USER_CACHE_KEY_PREFIX + userId;
        return (UserFeedDto) redisNewsFeedTemplate.opsForValue().get(key);
    }

    @Override
    public RedisPostDto getPost(Long postId) {
        String key = POST_CACHE_KEY_PREFIX + postId;
        // Corrected: Use the template configured for strings to retrieve the JSON string
        String serializedPost = redisNewsFeedStringLuaTemplate.opsForValue().get(key);
        log.info("GetPost: serializedPost: {}", serializedPost);

        if (serializedPost == null) {
            log.warn("Post with ID {} not found in cache.", postId);
            return null;
        }

        try {
            // Corrected: Use objectMapper.readValue to deserialize the JSON string
            RedisPostDto post = objectMapper.readValue(serializedPost, RedisPostDto.class);
            log.info("GetPost: post: {}", post);
            return post;
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize post {} from cache: {}", postId, e.getMessage());
            return null;
        }
    }

    @Override
    public List<RedisPostDto> getPostsBatch(List<Long> postIds) {
        List<String> keys = postIds.stream()
                .map(id -> POST_CACHE_KEY_PREFIX + id)
                .collect(Collectors.toList());
        List<Object> postObjects = redisNewsFeedTemplate.opsForValue().multiGet(keys);
        if (postObjects == null || postObjects.isEmpty()) return List.of();
        return postObjects
                .stream()
                .map(obj -> objectMapper.convertValue(obj, RedisPostDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentFeedDto> getComments(Long postId) {
        String key = POST_COMMENTS_KEY_PREFIX + postId;
        // Получаем элементы ZSET в обратном порядке (от новых к старым)
        Set<Object> serializedComments = redisNewsFeedTemplate.opsForZSet().reverseRange(key, 0, -1);
        if (serializedComments == null || serializedComments.isEmpty()) {
            List<CommentFeedDto> comments = commentRepository.findLastCommentsForPost(postId, newsFeedConfiguration.getCommentsLimit());
            comments.forEach(this::putComment);
            return comments;
        }

        List<CommentFeedDto> comments = new ArrayList<>();
        for (Object serializedComment : serializedComments) {
            if (serializedComment instanceof String) {
                try {
                    CommentFeedDto comment = objectMapper.readValue(
                            (String) serializedComment,
                            CommentFeedDto.class
                    );
                    comments.add(comment);
                } catch (JsonProcessingException e) {
                    log.error("Failed to deserialize comment for post {}: {}", postId, e.getMessage());
                }
            } else {
                log.warn("Unexpected comment type in Redis for post {}: {}",
                        postId, serializedComment.getClass());
            }
        }

        log.info("Comments: {}", comments);
        return comments;
    }

    @Override
    public boolean updatePost(long postId, String event) {
        // We no longer need to get the post here since the Lua script will handle it.
        // The previous code was failing here due to the deserialization issue.

        String postKey = POST_CACHE_KEY_PREFIX + postId;

        // Corrected: Use the template configured for strings
        Long result = redisNewsFeedStringLuaTemplate.execute(
                incrementPostCounterScript,
                Collections.singletonList(postKey),
                event
        );
        log.info("result: {}", result);
        if (result != null && result == 1L) {
            log.info("Successfully updated post {} counter for event {}.", postId, event);
            return true;
        } else {
            // The post might not be in the cache, so we load it from the DB and put it back.
            // This is the fallback logic that was previously attempted but failed.
            try {
                RedisPostDto postToUpdate = postRepository.findPostForRedisByPostId(postId)
                        .orElseThrow(() -> new EntityNotFoundException("Post doesn't exist in cache or db"));
                putPost(postToUpdate);
                log.info("Post with ID {} was not in cache. Loaded from DB and re-cached.", postId);
                return updatePost(postId, event); // Retry the update
            } catch (EntityNotFoundException e) {
                log.warn("Failed to update post {} counter for event {}. Post not found in DB.", postId, event);
                return false;
            }
        }
    }

    @Override
    public List<Long> getFeed(Long userId, long start, long end) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        Set<Object> postIds = redisNewsFeedTemplate.opsForZSet().reverseRange(key, start, end);
        if (postIds == null) {
            return Collections.emptyList();
        }

        return postIds.stream()
                .map(id -> {
                    if (id instanceof Integer) {
                        return ((Integer) id).longValue();
                    }
                    return (Long) id;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public void putFeedForSubscribers(Long userId, List<Long> followerIds) {
        Pageable pageable = PageRequest.of(0, newsFeedConfiguration.getFeedSize());
        // Это ведь вызывается по несколько раз? может сначала проверять что посты пользователя уже есть в кэше7
        List<RedisPostDto> posts = postRepository.findLatestPostsByAuthorId(userId, pageable);
        log.info("found {} Posts for user with ID {}", posts.size(), userId);

        if (posts.isEmpty()) {
            log.info("No posts found for user {}. Skipping feed update for followers.", userId);
            return;
        }

        putPostsBatch(posts);

        List<String> args = new ArrayList<>();
        args.addAll(followerIds.stream().map(String::valueOf).toList());
        args.add("POSTS_START");

        posts.forEach(post -> {
            args.add(String.valueOf(post.getCreatedAt().toEpochSecond(ZoneOffset.UTC)));
            args.add(String.valueOf(post.getPostId()));
        });

        long ttlInSeconds = Duration.ofMinutes(newsFeedConfiguration.getUserTtl() * 10L).getSeconds();

        redisNewsFeedStringLuaTemplate.execute(
                batchMultiPostFeedUpdateScript,
                List.of(String.valueOf(ttlInSeconds), String.valueOf(newsFeedConfiguration.getFeedSize())),
                args.toArray()
        );
        log.info("Added {} posts from author {} to {} followers' feeds.", posts.size(), userId, followerIds.size());
    }

    @Override
    public Long getPostRank(long userId, Long postId) {
        String key = FEED_CACHE_KEY_PREFIX + userId;
        return redisNewsFeedTemplate.opsForZSet().reverseRank(key, postId);
    }

    @Override
    public void putPostsBatch(List<RedisPostDto> posts) {
        if (posts.isEmpty()) {
            return;
        }

        List<String> keys = new ArrayList<>();
        List<String> serializedPosts = new ArrayList<>();

        posts.forEach(post -> {
            post.setViewCount(post.getViewCount() == null ? 0L : post.getViewCount());
            post.setVersion(post.getVersion() == null ? 0L : post.getVersion());
            try {
                String serializedPost = objectMapper.writeValueAsString(post);
                keys.add(POST_CACHE_KEY_PREFIX + post.getPostId());
                serializedPosts.add(serializedPost);
                log.info("serializedPost: {}", serializedPost);
            } catch (JsonProcessingException e) {
                log.error("Failed to serialize post {}: {}", post.getPostId(), e.getMessage());
            }
        });

        if (serializedPosts.isEmpty()) return;

        long ttlInSeconds = Duration.ofMinutes(newsFeedConfiguration.getPostTtl() * 10L).getSeconds();

        Object[] args = new Object[serializedPosts.size() + 1];
        args[0] = String.valueOf(ttlInSeconds);
        System.arraycopy(serializedPosts.toArray(), 0, args, 1, serializedPosts.size());

        redisNewsFeedStringLuaTemplate.execute(
                batchSetAndExpireScript,
                keys,
                args
        );

        log.info("Successfully cached {} posts in a batch with atomic TTL setting.", posts.size());
    }

    @Override
    public boolean putComment(CommentFeedDto dto) {
        String key = POST_COMMENTS_KEY_PREFIX + dto.postId();
        long score = dto.createdAt().toEpochSecond(ZoneOffset.UTC);

        String serializedComment;
        try {
            serializedComment = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize CommentDto: {}", dto, e);
            return false;
        }
        List<Object> scriptArgs = new ArrayList<>();
        scriptArgs.add(newsFeedConfiguration.getCommentsLimit());
        scriptArgs.add(score);
        scriptArgs.add(serializedComment);

        return redisNewsFeedTemplate.execute(
                addCommentAndTrimScript,
                Collections.singletonList(key),
                scriptArgs.toArray()
        ) > 0;
    }
}