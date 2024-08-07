package faang.school.postservice.cache.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class FeedCache {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ZSetOperations<String, Object> operations;

    @Value("${spring.data.redis.key-spaces.feed.prefix}")
    private String keyPrefix;

    @Value("${spring.data.redis.max-feed-size}")
    private int maxSize;

    @Value("${spring.data.redis.key-spaces.feed.ttl}")
    private long feedTTL;

    public FeedCache(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.operations = redisTemplate.opsForZSet();
    }

    public void save(Long userId, Long postId) {
        String key = keyPrefix + userId;
        operations.add(key, postId, System.currentTimeMillis());
        redisTemplate.expire(key, feedTTL, TimeUnit.DAYS);
    }

    public Set<Object> getFeed(Long userId) {
        return operations.reverseRange(keyPrefix + userId, 0, maxSize - 1);
    }
}