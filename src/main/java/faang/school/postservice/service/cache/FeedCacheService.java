package faang.school.postservice.service.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheService {

    private static final String FEED_KEY_PREFIX = "feed:";
    private static final String POST_KEY_PREFIX = "post:";
    private static final String USER_KEY_PREFIX = "user:";
    private static final String HEAT_JOB_PREFIX = "feedheat:";

    @Value("${app.feed.cache.feed-size:500}")
    private int feedSize;

    @Value("${app.feed.cache.feed-ttl-hours:24}")
    private long feedTtlHours;

    @Value("${app.feed.cache.post-ttl-hours:24}")
    private long postTtlHours;

    @Value("${app.feed.cache.user-ttl-hours:24}")
    private long userTtlHours;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Saves user feed to Redis using batch operations for performance.
     * Structure: feed:{userId} -> Sorted Set with postId and score (timestamp)
     * 
     * Optimizations:
     * - Batch ZADD operation
     * - Atomic trim using negative ranks
     */
    public void saveUserFeed(Long userId, List<Post> posts) {
        String feedKey = FEED_KEY_PREFIX + userId;
        
        if (posts.isEmpty()) {
            log.debug("Empty feed for user {}", userId);
            return;
        }

        Set<ZSetOperations.TypedTuple<String>> tuples = posts.stream()
            .map(post -> {
                double score = post.getPublishedAt() != null 
                    ? post.getPublishedAt().toEpochSecond(java.time.ZoneOffset.UTC)
                    : Instant.now().getEpochSecond();
                return ZSetOperations.TypedTuple.of(
                    String.valueOf(post.getId()), 
                    score
                );
            })
            .collect(Collectors.toSet());

        redisTemplate.opsForZSet().add(feedKey, tuples);

        redisTemplate.opsForZSet().removeRange(feedKey, 0, -(feedSize + 1));

        redisTemplate.expire(feedKey, Duration.ofHours(feedTtlHours));
        
        log.debug("Saved feed for user {}: {} posts (pipelined)", userId, posts.size());
    }

    /**
     * Saves post to Redis using putAll for performance.
     * TTL >= feed TTL for consistency.
     */
    public void savePost(Post post, List<Comment> comments, List<Like> likes) {
        String postKey = POST_KEY_PREFIX + post.getId();
        
        try {
            Map<String, String> postData = new HashMap<>();
            postData.put("id", String.valueOf(post.getId()));
            postData.put("content", post.getContent());
            postData.put("authorId", String.valueOf(post.getAuthorId()));
            
            if (post.getProjectId() != null) {
                postData.put("projectId", String.valueOf(post.getProjectId()));
            }
            
            postData.put("published", String.valueOf(post.isPublished()));
            
            if (post.getPublishedAt() != null) {
                postData.put("publishedAt", post.getPublishedAt().toString());
            }

            postData.put("likes", String.valueOf(likes.size()));
            postData.put("comments", String.valueOf(comments.size()));

            if (!comments.isEmpty()) {
                String commentsJson = objectMapper.writeValueAsString(
                    comments.stream()
                        .map(c -> Map.of(
                            "id", c.getId(),
                            "content", c.getContent(),
                            "authorId", c.getAuthorId(),
                            "createdAt", c.getCreatedAt().toString()
                        ))
                        .collect(Collectors.toList())
                );
                postData.put("commentsData", commentsJson);
            }

            redisTemplate.opsForHash().putAll(postKey, postData);

            redisTemplate.expire(postKey, Duration.ofHours(postTtlHours));
            
            log.debug("Saved post {} to cache (putAll)", post.getId());
        } catch (JsonProcessingException e) {
            log.error("Error serializing comments for post {}", post.getId(), e);
        }
    }

    /**
     * Saves user to Redis using putAll for performance.
     */
    public void saveUser(UserDto user) {
        String userKey = USER_KEY_PREFIX + user.id();

        Map<String, String> userData = new HashMap<>();
        userData.put("id", String.valueOf(user.id()));
        userData.put("username", user.username());
        userData.put("email", user.email());
        
        redisTemplate.opsForHash().putAll(userKey, userData);

        redisTemplate.expire(userKey, Duration.ofHours(userTtlHours));
        
        log.debug("Saved user {} to cache (putAll)", user.id());
    }

    /**
     * Checks if feed was already heated for user within specific job.
     * Used for idempotency.
     */
    public boolean isFeedHeatedForJob(Long userId, String jobId) {
        String jobKey = HEAT_JOB_PREFIX + jobId + ":" + userId;
        Boolean exists = redisTemplate.hasKey(jobKey);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * Marks feed as heated for specific job.
     * Used for idempotency.
     */
    public void markFeedHeatedForJob(Long userId, String jobId) {
        String jobKey = HEAT_JOB_PREFIX + jobId + ":" + userId;
        redisTemplate.opsForValue().set(jobKey, "done", Duration.ofHours(feedTtlHours));
    }

    /**
     * Gets list of post IDs from user feed.
     */
    public List<Long> getUserFeedPostIds(Long userId) {
        String feedKey = FEED_KEY_PREFIX + userId;
        Set<String> postIds = redisTemplate.opsForZSet().reverseRange(feedKey, 0, feedSize - 1);
        
        if (postIds == null) {
            return List.of();
        }
        
        return postIds.stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());
    }
}
