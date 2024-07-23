package faang.school.postservice.redis.cache;

import faang.school.postservice.config.redis.RedisProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

    public void addNewValueToZSet(String key, String value) {
        feedRedisTemplate.execute(new SessionCallback<>() {
            @Override
            public Object execute(RedisOperations operations) {
                boolean updated = false;
                while (!updated) {
                    operations.watch(key);

                    operations.multi();

                    getAddNewZSetValueRunnable(operations, key, value).run();

                    List<Object> results = operations.exec();
                    if (results.size() == 0) {
                        continue;
                    }

                    updated = true;
                }
                return null;
            }
        });
    }

    private Runnable getAddNewZSetValueRunnable(RedisOperations operations, String followerId, String postId) {
       return () ->{
           ZSetOperations<String, String> zSetOperations = operations.opsForZSet();

           Long currentFeedSize = zSetOperations.size(followerId);

           if (currentFeedSize != null && currentFeedSize == maxPostsAmount) {
               zSetOperations.popMin(followerId);
           }

           double score = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
           zSetOperations.add(followerId, postId, score);

           operations.expire(followerId, Duration.of(feedTtl, ChronoUnit.DAYS));
       };
    }
}
