package faang.school.postservice.repository.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
@RequiredArgsConstructor
public class NewsFeedRedisRepository {
    private final RedisTemplate<String, Long> redisTemplate;

    @Value("${spring.data.redis.cache.news-feed.ttl-hours}")
    private int ttlInHours;

    public void addPostId(String key, Long postId) {
        redisTemplate.opsForZSet().add(key, postId, -postId);
        redisTemplate.expire(key, ttlInHours, TimeUnit.HOURS);
    }

    public Set<Long> getSortedPostIds(String key) {
        return redisTemplate.opsForZSet().range(key, 0, -1);
    }

    public void removePostId(String key, Long postId) {
        redisTemplate.opsForZSet().remove(key, postId);
    }

    public void removeLastPostId(String key) {
        Objects.requireNonNull(redisTemplate.opsForZSet().range(key, -1, -1))
                .stream()
                .findFirst()
                .ifPresent(postId -> removePostId(key, postId));
    }

    public Long getSize(String key) {
        return redisTemplate.opsForZSet().size(key);
    }
}
