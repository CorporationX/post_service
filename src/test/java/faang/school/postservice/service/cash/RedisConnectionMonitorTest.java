package faang.school.postservice.service.cash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RedisConnectionMonitorTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private CacheWarmerService cacheWarmerService;

    @InjectMocks
    private RedisConnectionMonitor redisConnectionMonitor;

    @Mock
    private org.slf4j.Logger log;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Attempt reconnection with successful reconnection")
    void attemptReconnection_SuccessfulReconnection() {
        RedisConnection mockConnection = mock(RedisConnection.class);
        RedisServerCommands mockServerCommands = mock(RedisServerCommands.class);
        when(mockConnection.ping()).thenReturn("PONG");
        when(mockConnection.serverCommands()).thenReturn(mockServerCommands);
        when(mockServerCommands.dbSize()).thenReturn(10L);
        when(redisTemplate.execute(any(RedisCallback.class))).thenAnswer(invocation -> {
            RedisCallback<?> callback = invocation.getArgument(0);
            return callback.doInRedis(mockConnection);
        });
        redisConnectionMonitor.attemptReconnection();
        verify(redisTemplate, times(2)).execute((RedisCallback<String>) any());
        verify(cacheWarmerService, never()).warmUpCache();
    }

    @Test
    @DisplayName("Connection failure should trigger attempt to reconnect")
    void onConnectionFailure_ShouldCallAttemptReconnection() {
        RedisConnectionFailureException event = new RedisConnectionFailureException("Redis connection failed");
        RedisConnectionMonitor spyMonitor = spy(redisConnectionMonitor);
        spyMonitor.onConnectionFailure(event);
        verify(spyMonitor).attemptReconnection();
    }

    @Test
    @DisplayName("Attempt reconnection with connection error should throw exception")
    void attemptReconnection_ConnectionError_ThrowsException() {
        when(redisTemplate.execute(any(RedisCallback.class)))
                .thenThrow(new JedisConnectionException("Redis connection error"));
        assertThrows(JedisConnectionException.class, () -> {
            redisConnectionMonitor.attemptReconnection();
        });
        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
    }

    @Test
    @DisplayName("Health check with successful connection")
    void healthCheck_SuccessfulHealthCheck() {
        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn("PONG");
        redisConnectionMonitor.healthCheck();
        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
    }

    @Test
    @DisplayName("No cache warm-up when Redis database is not empty")
    void testNoCacheWarmupWhenRedisIsNotEmpty() {
        when(redisTemplate.execute(any(RedisCallback.class)))
                .thenReturn(10L);
        redisConnectionMonitor.attemptReconnection();
        verify(log, never()).info("Redis cache is empty after reconnection, starting warm-up");
        verify(cacheWarmerService, never()).warmUpCache();
    }

    @Test
    @DisplayName("Attempt reconnection handling generic connection errors")
    void testAttemptReconnectionWithConnectionError() {
        doThrow(new RuntimeException("Reconnection failed"))
                .when(redisTemplate)
                .execute(any(RedisCallback.class));
        try {
            redisConnectionMonitor.attemptReconnection();
        } catch (RuntimeException e) {
            verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
        }
    }
}