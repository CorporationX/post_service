package faang.school.postservice.service.cash;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisStartupListener implements ApplicationListener<ApplicationReadyEvent> {
    private final StringRedisTemplate redisTemplate;
    private final CacheWarmerService cacheWarmerService;

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 5000;

    @Override
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        log.info("Application started, checking Redis state");
        checkRedisAndWarmUp();
    }

    @Scheduled(fixedRate = 30000)
    public void scheduledCheck() {
        checkRedisAndWarmUp();
    }

    private void checkRedisAndWarmUp() {
        for (int attempts = 0; attempts < MAX_RETRY_ATTEMPTS; attempts++) {
            try {
                if (!isRedisConnected()) {
                    log.error("Failed to connect to Redis (attempt {}/{})", attempts + 1, MAX_RETRY_ATTEMPTS);
                    Thread.sleep(RETRY_DELAY_MS);
                    continue;
                }
                if (isRedisCacheEmpty()) {
                    log.info("Redis cache is empty, starting warm-up process");
                    cacheWarmerService.warmUpCache();
                } else {
                    log.debug("Redis cache is not empty, skipping warm-up");
                }
                return;

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            } catch (Exception e) {
                log.error("Unexpected error while checking Redis state", e);
                return;
            }
        }
        log.error("Failed to connect to Redis after {} attempts", MAX_RETRY_ATTEMPTS);
    }

    public boolean isRedisConnected() {
        try {
            redisTemplate.execute((RedisCallback<String>) connection -> {
                connection.ping();
                return null;
            });
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isRedisCacheEmpty() {
        try {
            Long size = redisTemplate.execute((RedisCallback<Long>) connection ->
                    connection.serverCommands().dbSize());
            return size != null && size == 0;
        } catch (Exception e) {
            log.error("Error checking Redis cache size", e);
            return false;
        }
    }
}