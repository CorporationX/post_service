package faang.school.postservice.redis.cache;

import faang.school.postservice.config.redis.RedisProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public class ZSetFeed {
    private final RedisTemplate<String, Object> feedRedisTemplate;
    private final RedisProperties redisProperties;
    private int feedTtl;
    private int maxPostsAmount;
    private ZSetOperations<String, Object> zSet;

    @PostConstruct
    public void setUp() {
        feedTtl = redisProperties.getFeedCache().getTtl();
        maxPostsAmount = redisProperties.getFeedCache().getMaxPostsAmount();
        zSet = feedRedisTemplate.opsForZSet();
    }

    //TODO: заменить текущую реализацию кеша постов на эту
    @Transactional
    public void addNewPost(String followerId, String postId) {
        Long currentFeedSize = zSet.size(followerId);

        if (currentFeedSize == null) {
            return;
        }

        if (currentFeedSize == maxPostsAmount) {
            zSet.popMin(followerId);
        }

        double score = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        zSet.add(followerId, postId, score);

        feedRedisTemplate.expire(followerId, Duration.of(feedTtl, ChronoUnit.DAYS));
    }
}
