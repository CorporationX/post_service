package faang.school.postservice.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.EnableRetry;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {
    @InjectMocks
    private RedisCacheService redisCacheService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    private Long userId;
    private Long postId;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisCacheService, "ttl", 1);
        ReflectionTestUtils.setField(redisCacheService, "maxSizeFeed", 100);
        ReflectionTestUtils.setField(redisCacheService, "Feed", "feed:");
        ReflectionTestUtils.setField(redisCacheService, "timestamp", ":timestamp");

        postId = 123L;
        userId = 1L;
    }

    @Test
    void saveToCache_successful() {
        String pattern = "testPattern";
        Long key = 1L;
        Object value = new Object();
        String jsonValue = "{}";
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);

        try {
            when(objectMapper.writeValueAsString(value)).thenReturn(jsonValue);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        redisCacheService.saveToCache(pattern, key, value);

        verify(hashOperations, times(1)).put(pattern, key.toString(), jsonValue);
        verify(redisTemplate, times(1)).expire(pattern, 1, TimeUnit.DAYS);
    }

    @Test
    public void testAddPostToUserFeedSuccess() {
        String key = "feed:" + userId;

        when(redisTemplate.execute((RedisCallback<Object>) any())).thenReturn(true);
        when(zSetOperations.size(key)).thenReturn(50L);

        redisCacheService.addPostToUserFeed(postId, userId);

        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
        verify(zSetOperations, times(1)).size(key);
        verify(zSetOperations, never()).removeRange(anyString(), anyLong(), anyLong());
    }

    @Test
    void testAddPostToUserFeedRetryable() {
        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(false);

        assertThrows(OptimisticLockingFailureException.class, () -> {
            redisCacheService.addPostToUserFeed(postId, userId);
        });

        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
    }

    @Test
    void testAddPostToUserFeedExceedsMaxSize() {
        String key = "feed:" + userId;

        when(redisTemplate.execute(any(RedisCallback.class))).thenReturn(true);
        when(zSetOperations.size(eq(key))).thenReturn(150L);

        redisCacheService.addPostToUserFeed(postId, userId);

        verify(redisTemplate, times(1)).execute(any(RedisCallback.class));
        verify(zSetOperations, times(1)).size(eq(key));
        verify(zSetOperations, times(1)).removeRange(eq(key), eq(0L), eq(0L));
    }
}
