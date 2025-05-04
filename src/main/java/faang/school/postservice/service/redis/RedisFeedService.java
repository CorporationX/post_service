package faang.school.postservice.service.redis;

import faang.school.postservice.config.redis.properties.FeedCacheProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
public class RedisFeedService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final FeedCacheProperties feedCacheProperties;

    private final AtomicLong counter = new AtomicLong(0);

    private String keyPrefix;

    @PostConstruct
    public void init() {
        this.keyPrefix = feedCacheProperties.name() + ":";
    }

    public void addPostId(long userId, LocalDateTime postCreatedAt, long postId) {
        String key = keyPrefix + userId;

        long timestamp = postCreatedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long currentCounter = counter.getAndIncrement();
        double score = timestamp + (currentCounter % 1000 / 1000.0);

        redisTemplate.opsForZSet().add(key, postId, score);
    }

    public Long getPostsIdsListSize(long userId) {
        String key = keyPrefix + userId;
        return redisTemplate.opsForZSet().size(key);
    }

    public void removeFirstPostIdIfNecessary(long userId) {
        String key = keyPrefix + userId;
        Long size = getPostsIdsListSize(userId);
        if (size != null && size > feedCacheProperties.batch()) {
            redisTemplate.opsForZSet().removeRange(key, 0, 0);
        }
    }

    public void addTtlForUserPostsIdsListIfNecessary(long userId) {
        Long size = getPostsIdsListSize(userId);
        if (size == null || size == 0) {
            String key = keyPrefix + userId;
            redisTemplate.expire(key, feedCacheProperties.timeToLive());
        }
    }

    public Set<Object> getPostsIdsListInReverseOrder(long userId) {
        String key = keyPrefix + userId;
        return redisTemplate.opsForZSet().reverseRange(key, 0, -1);
    }
}
