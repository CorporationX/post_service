package faang.school.postservice.repository.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedCacheRepository {

    @Value("${redis.cache.feed.key-prefix}")
    private String feedKeyPrefix;

    @Value("${redis.cache.feed.max-size}")
    private int maxSize;

    @Value("${redis.cache.feed.ttl-days}")
    private int ttlDays;

    private final StringRedisTemplate redisTemplate;

    public void save(Long subscriberId, Long postId, Instant postCreatedAt) {
        String key = feedKeyPrefix + subscriberId;
        double score = postCreatedAt.toEpochMilli();

        BoundZSetOperations<String, String> feed =
                redisTemplate.boundZSetOps(key);

        feed.add(postId.toString(), score);
        feed.removeRange(0, -maxSize - 1);
        redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
        log.debug("Post {} added to feed of follower {}", postId, subscriberId);
    }
}