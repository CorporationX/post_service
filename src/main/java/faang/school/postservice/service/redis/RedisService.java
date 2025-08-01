package faang.school.postservice.service.redis;

import faang.school.postservice.properties.RedisTtlProperties;
import faang.school.postservice.util.ObjectMapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private static final String LOCK_PREFIX = "lock";

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, String> redisTemplate;
    private final RedisTtlProperties ttlProperties;
    private final ObjectMapperUtil objectMapperUtil;

    public boolean keyPresent(String key) {
        return redisTemplate.hasKey(key);
    }

    public void saveAsString(String key, Object value, long ttl, TimeUnit timeUnit) {
        String json = objectMapperUtil.asString(value);
        redisTemplate.opsForValue().set(key, json, ttl, timeUnit);
    }

    public <T> T getWithUpdateTtlAs(String key, long ttl, TimeUnit timeUnit, Class<T> type) {
        String json = redisTemplate.opsForValue().getAndExpire(key, ttl, timeUnit);
        return objectMapperUtil.as(json, type);
    }

    public void incrementByKey(String key) {
        redisTemplate.opsForValue().increment(key, 1);
    }

    public long getCounterByKey(String key) {
        String raw = redisTemplate.opsForValue().get(key);

        if (raw == null) {
            throw new NoSuchElementException("Counter with key %s not found".formatted(key));
        }

        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wrong type of saved value");
        }
    }

    public void saveWithUpdateTtlZSetWithLockAndLimit(String key, String value, long score, int limit) {
        RLock lock = redissonClient.getLock(getKey(LOCK_PREFIX, key));
        try {
            lock.lock(ttlProperties.getLockTimeoutSeconds(), TimeUnit.SECONDS);
            redisTemplate.opsForZSet().add(key, value, score);

            Long size = redisTemplate.opsForZSet().size(key);
            if (size != null && size > limit) {
                redisTemplate.opsForZSet().removeRange(key, 0, size - limit - 1);
            }
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    public String getKey(String prefix, Object id) {
        return prefix + ":" + id.toString();
    }
}