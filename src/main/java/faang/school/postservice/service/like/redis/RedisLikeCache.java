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

    public void incrementLikes(String keyName, Long Id) {
        log.info("Incrementing likes for {}: {}", keyName, Id);
        redisCacheExecutor.execute(() -> {
            String key = keyName + Id;
            redisTemplate.opsForValue().increment(key);
        });
    }

    public void decrementLikes(String keyName, Long Id) {
        log.info("Decrementing likes for {}: {}", keyName, Id);
        redisCacheExecutor.execute(() -> {
            String key = keyName + Id;
            redisTemplate.opsForValue().decrement(key);
        });
    }
}
