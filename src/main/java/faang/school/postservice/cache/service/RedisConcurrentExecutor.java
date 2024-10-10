package faang.school.postservice.cache.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.redis.util.RedisLockRegistry;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisConcurrentExecutor {
    private final RedisLockRegistry redisLockRegistry;
    @Value("${spring.data.redis.lock-registry.try-lock-millis}")
    private long tryLockMillis;
    public void execute(String key, Runnable runnable, String action) {
        log.info("{} to {}", action, key);
        Lock lock = redisLockRegistry.obtain(key);
        try {
            if (lock.tryLock(tryLockMillis, TimeUnit.MILLISECONDS)) {
                log.info("Key {} locked for {}", key, action);
                try {
                    runnable.run();
                } finally {
                    lock.unlock();
                    log.info("Key {} unlocked after {}", key, action);
                }
            } else {
                log.warn("Failed to acquire lock for {}", key);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
