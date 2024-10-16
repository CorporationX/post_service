package faang.school.postservice.service.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCache {

    private final ConcurrentMap<String, Object> locks = new ConcurrentHashMap<>();
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, String> zSetOperations;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.settings.ttl}")
    private int ttl;
    @Value("${spring.data.redis.settings.maxSizeFeed}")
    private int maxSizeFeed;
    @Value("${spring.data.redis.settings.maxSizeComment}")
    private int maxSizeComment;
    @Value("${spring.data.redis.directory.feed}")
    private String feed;
    @Value("${spring.data.redis.directory.comment}")
    private String comment;
    @Value("${spring.data.redis.directory.like}")
    private String like;
    @Value("${spring.data.redis.directory.timestamp}")
    private String timestamp;

    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 300, multiplier = 3))
    public void saveToCache(String pattern, Long key, Object value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            String keyStr = key.toString();

            Boolean success = performHSetTransaction(pattern, keyStr, jsonValue);

            if (!success) {
                log.error("Failed to add pattern {} due to optimistic lock", pattern);
                throw new OptimisticLockingFailureException("Conflict while adding post");
            }

            log.debug("Saved key: {} with value: {} and patternType: {}", keyStr, jsonValue, pattern);
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for key: {}", key, e);
            throw new RuntimeException(e);
        }
    }

    public void addPostToUserFeed(Long postId, Long userId) {
        addPostToUserFeed(postId, userId, new Timestamp(System.currentTimeMillis()).getTime());
    }

    public void addPostToUserFeed(Long postId, Long userId, long updatedTime) {
        String key = feed + userId;
        String timestampKey = key + timestamp;
        String postIdStr = postId.toString();

        saveZSetOption(key, updatedTime, postIdStr, timestampKey);
        checkLimitZSet(key, maxSizeFeed);
    }

    public void addCommentToCache(Long postId, String commentFeedJson) {
        addCommentToCache(postId, commentFeedJson, new Timestamp(System.currentTimeMillis()).getTime());
    }

    public void addCommentToCache(Long postId, String commentFeedJson, long updatedTime) {
        String key = comment + postId;
        String timestampKey = key + timestamp;

        saveZSetOption(key, updatedTime, commentFeedJson, timestampKey);
        checkLimitZSet(key, maxSizeComment);
    }

    public void addLikeToCache(Long postId, String likeJson) {
        String key = like + postId;
        String timestampKey = key + timestamp;
        long currentTime = Instant.now().getEpochSecond();

        saveZSetOption(key, currentTime, likeJson, timestampKey);
    }

    public String getFromHSetCache(String pattern, String key) {

        return redisTemplate.execute((RedisCallback<String>) connection -> {
            byte[] valueBytes = connection.hGet(pattern.getBytes(), key.getBytes());
            return valueBytes != null ? new String(valueBytes) : null;
        });
    }

    public Set<String> getAllZSetValues(String key) {
        return zSetOperations.reverseRange(key, 0, -1);
    }

    public Long getZSetSize(String key) {
        return zSetOperations.zCard(key);
    }

    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 300, multiplier = 3))
    protected void saveZSetOption(String key, long currentTime, String value, String timestamp) {

        boolean success = performZSetTransaction(key, currentTime, value, timestamp);

        if (!success) {
            log.error("Failed to add post due to optimistic locking conflict.");
            throw new OptimisticLockingFailureException("Conflict while adding post.");
        }
    }

    protected void checkLimitZSet(String key, int maxSize) {
        synchronized (getLock(key)) {
            Long currentSize = zSetOperations.size(key);
            if (currentSize != null && currentSize > maxSize) {
                zSetOperations.removeRange(key, 0, 0);
            }
        }
    }

    private Object getLock(String key) {
        return locks.computeIfAbsent(key, k -> new Object());
    }

    private Boolean performHSetTransaction(String pattern, String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.multi();
            connection.hSet(pattern.getBytes(), key.getBytes(), value.getBytes());
            connection.expire(pattern.getBytes(), ttl);
            connection.exec();
            return true;
        }));
    }

    private boolean performZSetTransaction(String key, long currentTime, String value, String timestamp) {
        return Boolean.TRUE.equals(redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.multi();
            connection.zAdd(key.getBytes(), currentTime, value.getBytes());
            connection.set(timestamp.getBytes(), String.valueOf(currentTime).getBytes());
            connection.exec();
            return true;
        }));
    }
}
