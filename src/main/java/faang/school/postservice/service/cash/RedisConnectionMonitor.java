package faang.school.postservice.service.cash;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.exceptions.JedisConnectionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisConnectionMonitor {
    private final StringRedisTemplate redisTemplate;
    private final CacheWarmerService cacheWarmerService;

    @EventListener(RedisConnectionFailureException.class)
    public void onConnectionFailure(RedisConnectionFailureException event) {
        log.error("Redis connection failed. Attempting to reconnect...", event);
        attemptReconnection();
    }

    @Retryable(
            retryFor = {JedisConnectionException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void attemptReconnection() {
        try {
            redisTemplate.execute((RedisCallback<String>) connection -> {
                connection.ping();
                return null;
            });

            log.info("Successfully reconnected to Redis");

            Long size = redisTemplate.execute((RedisCallback<Long>) connection ->
                    connection.serverCommands().dbSize());

            if (size != null && size == 0) {
                log.info("Redis cache is empty after reconnection, starting warm-up");
                cacheWarmerService.warmUpCache();
            }
        } catch (Exception e) {
            log.error("Failed to reconnect to Redis", e);
            throw e;
        }
    }

    @Scheduled(fixedRate = 30000)
    public void healthCheck() {
        try {
            redisTemplate.execute((RedisCallback<String>) connection -> {
                connection.ping();
                return null;
            });
        } catch (Exception e) {
            log.error("Redis health check failed, attempting reconnection", e);
            attemptReconnection();
        }
    }
}