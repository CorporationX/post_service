package faang.school.postservice.repository.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class RedisFeedRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> zSet;

    @Value("${spring.data.redis.feed.prefix}")
    private String keyPrefix;

    @Value("${spring.data.redis.feed.max-feed-size}")
    private int maxSize;

    @Value("${spring.data.redis.feed.TTL}")
    private long feedTTL;

    public RedisFeedRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.zSet = redisTemplate.opsForZSet();
    }

    public void save(Long userId, Long postId) {
        try {
            String key = keyPrefix + userId;
            zSet.add(key, String.valueOf(postId), System.currentTimeMillis());
            redisTemplate.expire(key, feedTTL, TimeUnit.DAYS);
        }catch (Exception e) {
            log.error("Failed to add post to feed for user {}: {}", userId, e.getMessage(), e);
        }
    }

    public Set<Object> getFeed(Long userId) {
        return zSet.reverseRange(keyPrefix + userId, 0, maxSize - 1);
    }
}
