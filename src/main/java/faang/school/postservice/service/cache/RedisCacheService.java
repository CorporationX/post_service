package faang.school.postservice.service.cache;

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

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, String> zSetOperations;
    private final ObjectMapper objectMapper;
    private final Object lock = new Object();

    @Value("${spring.data.redis.settings.ttl}")
    private int ttl;
    @Value("${spring.data.redis.settings.maxSizeFeed}")
    private int maxSizeFeed;
    @Value("${spring.data.redis.directory.feed}")
    private String Feed;
    @Value("${spring.data.redis.directory.timestamp}")
    private String timestamp;

    public void saveToCache(String pattern, Long key, Object value) {
        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            String keyStr = key.toString();

            synchronized (lock) {
                redisTemplate.opsForHash().put(pattern, keyStr, jsonValue);
                redisTemplate.expire(pattern, ttl, TimeUnit.DAYS);
                log.debug("Saved key: {} with value: {} and patternType: {}", keyStr, jsonValue, pattern);
            }
        } catch (JsonProcessingException e) {
            log.error("Error processing JSON for key: {}", key, e);
            throw new RuntimeException(e);
        }
    }

    @Retryable(value = {OptimisticLockingFailureException.class}, maxAttempts = 5, backoff = @Backoff(delay = 300, multiplier = 3))
    public void addPostToUserFeed(Long postId, Long userId) {
        String key = Feed + userId;
        String timestampKey = key + timestamp;
        String postIdStr = postId.toString();
        long currentTime = Instant.now().getEpochSecond();

        Boolean success = redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            connection.multi();
            connection.zAdd((key + "key").getBytes(), currentTime, (postIdStr +"value").getBytes());
            connection.set(timestampKey.getBytes(), String.valueOf(currentTime).getBytes());
            connection.exec();
            return true;
        });

        if (!success) {
            log.error("Failed to add post due to optimistic locking conflict.");
            throw new OptimisticLockingFailureException("Conflict while adding post.");
        }

        Long currentSize = zSetOperations.size(key);
        if (currentSize != null && currentSize > maxSizeFeed) {
            zSetOperations.removeRange(key, 0, 0);
        }
    }
}
