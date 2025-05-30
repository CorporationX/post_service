package faang.school.postservice.service.newsfeed;

import faang.school.postservice.config.properties.RedisReconnectionProperties;
import faang.school.postservice.exception.RedisUnavailableException;
import io.lettuce.core.RedisConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisReconnectionService {

    private final StringRedisTemplate stringRedisTemplate;
    private final CacheWarmerService cacheWarmerService;
    private final RedisReconnectionProperties redisReconnectionProperties;
    private final ThreadPoolTaskScheduler scheduler;

    public RedisReconnectionService(
            StringRedisTemplate stringRedisTemplate,
            CacheWarmerService cacheWarmerService,
            RedisReconnectionProperties redisReconnectionProperties,
            @Qualifier("redisReconnectionScheduler") ThreadPoolTaskScheduler scheduler
    ) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.cacheWarmerService = cacheWarmerService;
        this.redisReconnectionProperties = redisReconnectionProperties;
        this.scheduler = scheduler;
    }

    private final Object reconnectionLock = new Object();
    private ScheduledFuture<?> reconnectionScheduledFuture;

    @Scheduled(fixedRateString = "#{@redisReconnectionProperties.healthCheckScheduler.fixedRateMs}")
    public void scheduledConnectionCheck() {
        if (isRedisUnavailable()) {
            log.info("Redis is unavailable, attempting scheduled reconnection...");
            startReconnectionTaskIfNeeded();
            try {
                attemptReconnection();
            } catch (RedisUnavailableException e) {
                log.warn("Scheduled reconnection attempt failed, will retry later", e);
            }
        } else {
            try {
                pingConnection();
                log.debug("Scheduled health check: Redis connection is healthy");
            } catch (RedisConnectionException ex) {
                log.warn("Redis is unreachable during scheduled health check. Triggering reconnection...", ex);
                try {
                    attemptReconnection();
                } catch (RedisUnavailableException e) {
                    log.warn("Initial reconnection attempt failed, switching to scheduled retry", e);
                }
            }
        }
    }

    public void pingConnection() {
        try {
            stringRedisTemplate.execute((RedisCallback<String>) connection -> {
                connection.ping();
                return null;
            });
            log.debug("Redis ping successful");
        } catch (RedisConnectionException ex) {
            log.error("Redis ping failed", ex);
            throw ex;
        }
    }

    @Retryable(
            retryFor = {RedisUnavailableException.class},
            backoff = @Backoff(
                    delayExpression = "#{@redisReconnectionProperties.retry.delayMs}",
                    multiplierExpression = "#{@redisReconnectionProperties.retry.multiplier}"
            )
    )
    public void attemptReconnection() {
        try {
            log.info("Attempting Redis reconnection...");
            pingConnection();
            setRedisUnavailable(false);
            log.info("Successfully reconnected to Redis");
            stopReconnectionTask();

            Long size = stringRedisTemplate.execute((RedisCallback<Long>) connection ->
                    connection.serverCommands().dbSize());

            if (size != null && size < redisReconnectionProperties.getMinCacheKeys()) {
                log.info("Redis cache is empty after reconnection. Warming up cache...");
                cacheWarmerService.warmUpCache();
            }
        } catch (RedisConnectionException ex) {
            log.error("Redis reconnection failed");
            setRedisUnavailable(true);
            throw new RedisUnavailableException("Redis reconnection failed", ex);
        }
    }

    @Recover
    public void recover(RedisUnavailableException ex) {
        log.error("Redis reconnection attempts exhausted. Switching to scheduled retry mode", ex);
        setRedisUnavailable(true);
        startReconnectionTaskIfNeeded();
    }

    private void startReconnectionTaskIfNeeded() {
        synchronized (reconnectionLock) {
            if (reconnectionScheduledFuture != null && !reconnectionScheduledFuture.isDone()) {
                return;
            }
            log.info("Scheduling periodic Redis reconnection attempts...");
            reconnectionScheduledFuture = scheduler.scheduleWithFixedDelay(() -> {
                try {
                    if (!isRedisUnavailable()) {
                        stopReconnectionTask();
                        log.info("Redis is available, stopping scheduled reconnection attempts");
                        return;
                    }
                    attemptReconnection();
                } catch (RedisUnavailableException e) {
                    log.warn("Reconnection attempt failed, will retry in {} seconds",
                            redisReconnectionProperties.getReconnectionIntervalSec(), e);
                } catch (Exception ex) {
                    log.error("Unexpected error in reconnection task", ex);
                }
            }, Duration.ofSeconds(redisReconnectionProperties.getReconnectionIntervalSec()));
        }
    }

    private void stopReconnectionTask() {
        synchronized (reconnectionLock) {
            if (reconnectionScheduledFuture != null && !reconnectionScheduledFuture.isDone()) {
                reconnectionScheduledFuture.cancel(true);
                log.info("Stopped scheduled reconnection attempts");
            }
            reconnectionScheduledFuture = null;
        }
    }

    private void setRedisUnavailable(boolean unavailable) {
        stringRedisTemplate.opsForValue().set(redisReconnectionProperties.getRedisUnavailableKey(), Boolean.toString(unavailable),
                redisReconnectionProperties.getRedisUnavailableTtlSec(), TimeUnit.SECONDS);
    }

    private boolean isRedisUnavailable() {
        String value = stringRedisTemplate.opsForValue().get(redisReconnectionProperties.getRedisUnavailableKey());
        return Boolean.parseBoolean(value);
    }
}
