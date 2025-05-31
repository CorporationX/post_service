package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
@RequiredArgsConstructor
public class PostViewCacheRepository {

    private final StringRedisTemplate redisTemplate;

    @Value("app.cache.posts.key-prefix")
    private String keyPrefix;

    public void incrementViews(Long postId) {
        redisTemplate.opsForZSet().incrementScore(keyPrefix + postId, "views", 1);
    }

    public void addViewDetails(Long postId, Long userId, Instant viewedAt) {
        redisTemplate.opsForZSet()
                .add(keyPrefix + postId, userId + ":" + viewedAt.toEpochMilli(), viewedAt.toEpochMilli());
    }
}
