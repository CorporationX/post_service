package faang.school.postservice.service.cash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RedisStartupListenerTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private CacheWarmerService cacheWarmerService;

    @Mock
    private ApplicationReadyEvent event;

    @Mock
    private RedisConnection redisConnection;

    @Mock
    private RedisServerCommands redisServerCommands;

    @InjectMocks
    private RedisStartupListener redisStartupListener;

    @BeforeEach
    void setUp() {
        when(redisConnection.serverCommands()).thenReturn(redisServerCommands);
    }

    @Test
    @DisplayName("On application event with Redis connected and empty cache, warm up cache")
    void onApplicationEvent_WhenRedisConnectedAndEmpty_ShouldWarmUpCache() {
        setupRedisConnection(true);
        setupEmptyCache(true);
        redisStartupListener.onApplicationEvent(event);
        verify(cacheWarmerService).warmUpCache();
    }

    @Test
    @DisplayName("On application event with Redis connected and non-empty cache, do not warm up")
    void onApplicationEvent_WhenRedisConnectedAndNotEmpty_ShouldNotWarmUpCache() {
        setupRedisConnection(true);
        setupEmptyCache(false);
        redisStartupListener.onApplicationEvent(event);
        verify(cacheWarmerService, never()).warmUpCache();
    }

    @Test
    @DisplayName("On application event with Redis not connected, retry without warming up cache")
    void onApplicationEvent_WhenRedisNotConnected_ShouldRetryAndNotWarmUpCache() {
        setupRedisConnection(false);
        redisStartupListener.onApplicationEvent(event);
        verify(cacheWarmerService, never()).warmUpCache();
    }

    @Test
    @DisplayName("Scheduled check with Redis connected and empty cache should warm up")
    void scheduledCheck_WhenRedisConnectedAndEmpty_ShouldWarmUpCache() {
        setupRedisConnection(true);
        setupEmptyCache(true);
        redisStartupListener.scheduledCheck();
        verify(cacheWarmerService).warmUpCache();
    }

    @Test
    @DisplayName("Scheduled check with Redis connected and non-empty cache should not warm up")
    void scheduledCheck_WhenRedisConnectedAndNotEmpty_ShouldNotWarmUpCache() {
        setupRedisConnection(true);
        setupEmptyCache(false);
        redisStartupListener.scheduledCheck();
        verify(cacheWarmerService, never()).warmUpCache();
    }

    @Test
    @DisplayName("Check Redis connection - successful connection returns true")
    void isRedisConnected_WhenConnectionSuccessful_ShouldReturnTrue() {
        setupRedisConnection(true);
        boolean result = redisStartupListener.isRedisConnected();
        assertTrue(result);
    }

    @Test
    @DisplayName("Check Redis connection - failed connection returns false")
    void isRedisConnected_WhenConnectionFails_ShouldReturnFalse() {
        setupRedisConnection(false);
        boolean result = redisStartupListener.isRedisConnected();
        assertFalse(result);
    }

    @Test
    @DisplayName("Check Redis cache - empty cache returns true")
    void isRedisCacheEmpty_WhenCacheEmpty_ShouldReturnTrue() {
        setupRedisConnection(true);
        setupEmptyCache(true);
        boolean result = redisStartupListener.isRedisCacheEmpty();
        assertTrue(result);
    }

    @Test
    @DisplayName("Check Redis cache - non-empty cache returns false")
    void isRedisCacheEmpty_WhenCacheNotEmpty_ShouldReturnFalse() {
          setupRedisConnection(true);
        setupEmptyCache(false);
        boolean result = redisStartupListener.isRedisCacheEmpty();
        assertFalse(result);
    }

    @Test
    @DisplayName("Check Redis cache - exception handling returns false")
    void isRedisCacheEmpty_WhenCheckingThrowsException_ShouldReturnFalse() {
        when(redisTemplate.execute(any(RedisCallback.class)))
                .thenThrow(new RuntimeException("Redis error"));
        boolean result = redisStartupListener.isRedisCacheEmpty();
        assertFalse(result);
    }

    @Test
    @DisplayName("Check Redis and warm up - handle thread interruption")
    void checkRedisAndWarmUp_WhenInterrupted_ShouldHandleInterruption() {
        doAnswer(invocation -> {
            Thread.currentThread().interrupt();
            return null;
        }).when(redisTemplate).execute(any(RedisCallback.class));
        redisStartupListener.scheduledCheck();
        assertTrue(Thread.interrupted()); // Clear interrupted status
        verify(cacheWarmerService, never()).warmUpCache();
    }
    private void setupRedisConnection(boolean isConnected) {
        if (isConnected) {
            when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> {
                RedisCallback<?> callback = invocation.getArgument(0);
                return callback.doInRedis(redisConnection);
            });
        } else {
            when(redisTemplate.execute(any(RedisCallback.class)))
                    .thenThrow(new RuntimeException("Connection failed"));
        }
    }

    private void setupEmptyCache(boolean isEmpty) {
        when(redisServerCommands.dbSize()).thenReturn(isEmpty ? 0L : 10L);
    }
}