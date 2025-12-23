package faang.school.postservice.service.redis_lock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisDistributedLock {

    private final StringRedisTemplate redisTemplate;

    @Value("${spring.redis.lock.timeout:5000}")
    private long lockTimeoutMs;

    @Value("${spring.redis.lock.wait-time:3000}")
    private long waitTimeMs;

    private static final String LOCK_PREFIX = "lock:";

    public boolean tryLock(String resource, String requestId) {
        return tryLock(resource, requestId, lockTimeoutMs, TimeUnit.MILLISECONDS);
    }

    public boolean tryLockWithWait(String resource, String requestId) {
        long endTime = System.currentTimeMillis() + waitTimeMs;

        while (System.currentTimeMillis() < endTime) {
            if (tryLock(resource, requestId)) {
                return true;
            }

            try {
                Thread.sleep(100); // Фиксированный интервал
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }

        return false;
    }

    public boolean tryLock(String resource, String requestId, long timeout, TimeUnit unit) {
        String lockKey = LOCK_PREFIX + resource;
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, requestId, timeout, unit);
        return Boolean.TRUE.equals(acquired);
    }

    public void unlock(String resource, String requestId) {
        String lockKey = LOCK_PREFIX + resource;
        String currentValue = redisTemplate.opsForValue().get(lockKey);

        if (requestId.equals(currentValue)) {
            redisTemplate.delete(lockKey);
        }
    }
}
