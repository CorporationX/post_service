package faang.school.postservice.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FeedCacheRepository {

    @Value("${cache.feed.collection}")
    private String collection;

    @Value("${cache.feed.ttl-days}")
    private int ttlDays;

    @Value("${cache.feed.max-size}")
    private int maxSize;

    private final StringRedisTemplate redisTemplate;

    public void save(Long followerId, Long postId, LocalDateTime postCreatedAt) {
        double score = postCreatedAt.toEpochSecond(ZoneOffset.UTC);
        String key = collection + followerId;

        redisTemplate.opsForZSet().add(key, postId.toString(), score);
        log.debug("Saved new post to cache. Key {}, post id {}", key, postId);

        Long size = redisTemplate.opsForZSet().size(key);
        if (size != null && size > maxSize) {
            redisTemplate.opsForZSet().removeRange(key, 0, size - maxSize - 1);

        }

        redisTemplate.expire(key, ttlDays, TimeUnit.DAYS);
    }
}