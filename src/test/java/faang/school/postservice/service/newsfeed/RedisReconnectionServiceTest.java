package faang.school.postservice.service.newsfeed;

import faang.school.postservice.config.properties.RedisReconnectionProperties;
import faang.school.postservice.exception.RedisUnavailableException;
import io.lettuce.core.RedisConnectionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisReconnectionServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private CacheWarmer cacheWarmer;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private RedisReconnectionProperties redisReconnectionProperties;

    @Mock
    private ThreadPoolTaskScheduler scheduler;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private RedisServerCommands redisServerCommands;

    @Mock
    private ScheduledFuture<?> scheduledFuture;

    @InjectMocks
    private RedisReconnectionService redisReconnectionService;

    private final String REDIS_UNAVAILABLE_KEY = "redis:unavailable";
    private final Long DB_SIZE_NORMAL = 100L;
    private final Long DB_SIZE_EMPTY_CACHE = 3L;
    private final Integer MIN_CACHE_KEYS = 5;
    private final Long REDIS_UNAVAILABLE_TTL_SEC = 300L;
    private final Integer RECONNECTION_INTERVAL_SEC = 2;


    @BeforeEach
    void setUp() {
        lenient().when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisReconnectionProperties.getRedisUnavailableKey()).thenReturn(REDIS_UNAVAILABLE_KEY);
        lenient().when(redisReconnectionProperties.getMinCacheKeys()).thenReturn(MIN_CACHE_KEYS);
        lenient().when(redisReconnectionProperties.getRedisUnavailableTtlSec())
                .thenReturn(REDIS_UNAVAILABLE_TTL_SEC.intValue());
        lenient().when(redisReconnectionProperties.getReconnectionIntervalSec()).thenReturn(RECONNECTION_INTERVAL_SEC);

        lenient().doAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            if (callback.toString().contains("ping")) {
                return null;
            }
            if (callback.toString().contains("dbSize")) {
                return DB_SIZE_NORMAL;
            }
            return null;
        }).when(stringRedisTemplate).execute(any(RedisCallback.class));
    }

    @Test
    void attemptReconnectionSuccessfullySetsRedisAvailableAndStopsTaskIfRunning() {
        doReturn(scheduledFuture).when(scheduler).scheduleWithFixedDelay(any(Runnable.class), any(Duration.class));
        redisReconnectionService.recover(new RedisUnavailableException("Simulating prior failure"));

        when(stringRedisTemplate.execute(any(RedisCallback.class)))
                .thenAnswer(invocation -> null)
                .thenAnswer(invocation -> DB_SIZE_NORMAL);
        when(scheduledFuture.isDone()).thenReturn(false);

        redisReconnectionService.attemptReconnection();

        verify(valueOperations).set(REDIS_UNAVAILABLE_KEY, "false", REDIS_UNAVAILABLE_TTL_SEC, TimeUnit.SECONDS);
        verify(scheduledFuture).cancel(true);
        verify(cacheWarmer, never()).warmUpCache();
    }

    @Test
    void attemptReconnectionSuccessfully() {
        when(stringRedisTemplate.execute(any(RedisCallback.class)))
                .thenAnswer(invocation -> null)
                .thenAnswer(invocation -> DB_SIZE_EMPTY_CACHE);

        redisReconnectionService.attemptReconnection();

        verify(valueOperations).set(REDIS_UNAVAILABLE_KEY, "false", REDIS_UNAVAILABLE_TTL_SEC, TimeUnit.SECONDS);
        verify(cacheWarmer).warmUpCache();
    }

    @Test
    void attemptReconnectionWhenPingFails() {
        doThrow(new RedisConnectionException("Ping failed"))
                .when(stringRedisTemplate).execute(any(RedisCallback.class));

        RedisUnavailableException exception = assertThrows(RedisUnavailableException.class,
                () -> redisReconnectionService.attemptReconnection());

        assertTrue(exception.getMessage().contains("Redis reconnection failed"));
        verify(valueOperations).set(REDIS_UNAVAILABLE_KEY, "true", REDIS_UNAVAILABLE_TTL_SEC, TimeUnit.SECONDS);
        verify(cacheWarmer, never()).warmUpCache();
    }

    @Test
    void TestRecover() {
        doReturn(scheduledFuture).when(scheduler).scheduleWithFixedDelay(any(Runnable.class), any(Duration.class));

        redisReconnectionService.recover(new RedisUnavailableException("Test recovery"));

        verify(valueOperations).set(REDIS_UNAVAILABLE_KEY, "true", REDIS_UNAVAILABLE_TTL_SEC, TimeUnit.SECONDS);
        verify(scheduler).scheduleWithFixedDelay(any(Runnable.class),
                eq(Duration.ofSeconds(RECONNECTION_INTERVAL_SEC)));
    }

    @Test
    void scheduledConnectionCheckWhenRedisIsUnavailable() {
        when(valueOperations.get(REDIS_UNAVAILABLE_KEY)).thenReturn("true");
        when(stringRedisTemplate.execute(any(RedisCallback.class)))
                .thenAnswer(invocation -> null)
                .thenAnswer(invocation -> DB_SIZE_NORMAL);

        doReturn(scheduledFuture).when(scheduler).scheduleWithFixedDelay(any(Runnable.class), any(Duration.class));

        redisReconnectionService.scheduledConnectionCheck();

        verify(valueOperations, times(1))
                .set(REDIS_UNAVAILABLE_KEY, "false", REDIS_UNAVAILABLE_TTL_SEC, TimeUnit.SECONDS);
        verify(scheduler).scheduleWithFixedDelay(any(Runnable.class), any(Duration.class));
    }

    @Test
    void scheduledConnectionCheckWhenRedisIsAvailableAndPingSuccessful() {
        when(valueOperations.get(REDIS_UNAVAILABLE_KEY)).thenReturn("false");

        redisReconnectionService.scheduledConnectionCheck();

        verify(stringRedisTemplate, times(1)).execute(any(RedisCallback.class));
        verify(valueOperations, never()).set(eq(REDIS_UNAVAILABLE_KEY), eq("false"), anyLong(), any());
        verify(scheduler, never()).scheduleWithFixedDelay(any(Runnable.class), any(Duration.class));
    }

    @Test
    void scheduledConnectionCheckWhenRedisIsAvailableButPingFails() {
        when(valueOperations.get(REDIS_UNAVAILABLE_KEY)).thenReturn("false");
        when(stringRedisTemplate.execute(any(RedisCallback.class)))
                .thenThrow(new RedisConnectionException("Ping failed"))
                .thenAnswer(invocation -> null)
                .thenAnswer(invocation -> DB_SIZE_NORMAL);


        redisReconnectionService.scheduledConnectionCheck();

        verify(valueOperations, times(1))
                .set(REDIS_UNAVAILABLE_KEY, "false", REDIS_UNAVAILABLE_TTL_SEC, TimeUnit.SECONDS);
    }
}
