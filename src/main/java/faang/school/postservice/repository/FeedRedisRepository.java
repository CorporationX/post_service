package faang.school.postservice.repository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class FeedRedisRepository {
    private static final String KEY_PREFIX = "user_id:";
    private final RedisTemplate<String, Long> redisTemplate;

    @Value("${spring.data.redis.feed.post-max-size}")
    private int postMaxSize;

    public void addToFeed(Long userId , Long postId) {
        ZSetOperations<String, Long> zSetOps = redisTemplate.opsForZSet();
        String key = KEY_PREFIX + userId;
        double score = -Instant.now().toEpochMilli();
        zSetOps.add(key, postId, score);
        zSetOps.removeRange(key, postMaxSize, -1);
    }
}
