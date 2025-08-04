package faang.school.postservice.service.like.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;


@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLikeCache {
    private final ExecutorService redisCacheExecutor;
    private final RedisTemplate<String, Object> redisTemplate;

    @Async(value = "redisCacheExecutor")
    public void incrementLikes(String keyName, Long objectId) {
        log.info("Incrementing likes for {}: {}", keyName, objectId);
        String key = keyName + objectId;
        redisTemplate.opsForValue().increment(key);
        log.info("Incrementing likes for {}: {}, is done", keyName, objectId);
    }

    @Async(value = "redisCacheExecutor")
    public void decrementLikes(String keyName, Long objectId) {
        log.info("Decrementing likes for {}: {}", keyName, objectId);
        String key = keyName + objectId;
        redisTemplate.opsForValue().decrement(key);
        log.info("Decrementing likes for {}: {}, is done", keyName, objectId);
    }
}
