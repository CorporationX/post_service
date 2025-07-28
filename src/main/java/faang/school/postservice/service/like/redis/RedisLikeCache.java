package faang.school.postservice.service.like.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLikeCache {
    private final ExecutorService redisCacheExecutor;
    private final RedisTemplate<String, Object> redisTemplate;

    public void incrementLikes(String keyName, Long objectId) {
        log.info("Incrementing likes for {}: {}", keyName, objectId);
        redisCacheExecutor.execute(() -> {
            String key = keyName + objectId;
            redisTemplate.opsForValue().increment(key);
        });
    }

    public void decrementLikes(String keyName, Long objectId) {
        log.info("Decrementing likes for {}: {}", keyName, objectId);
        redisCacheExecutor.execute(() -> {
            String key = keyName + objectId;
            redisTemplate.opsForValue().decrement(key);
        });
    }
}
