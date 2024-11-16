package faang.school.postservice.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.RedisTransactionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisListCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RedisListCacheService<Object> redisListCacheService;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private Runnable task;

    @Mock
    private RedisOperations<String, Object> mockOperations;

    @Captor
    private ArgumentCaptor<SessionCallback<List<Object>>> callbackCaptor;

    private String key;
    private String value;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
        key = "testKey";
        value = "testValue";
    }

    @Test
    void testPut() {
        Duration ttl = Duration.ofMinutes(5);

        redisListCacheService.put(key, value, ttl);

        verify(redisTemplate.opsForList()).leftPush(key, value);
    }

    @Test
    void testSize() {
        Long expectedSize = 5L;
        when(redisTemplate.opsForList().size(key)).thenReturn(expectedSize);

        long size = redisListCacheService.size(key);

        assertEquals(expectedSize, size);
        verify(redisTemplate.opsForList()).size(key);
    }

    @Test
    void testSizeWhenNull() {
        when(redisTemplate.opsForList().size(key)).thenReturn(null);

        long size = redisListCacheService.size(key);

        assertEquals(0L, size);
        verify(redisTemplate.opsForList()).size(key);
    }

    @Test
    void testRunInOptimisticLock() {
        when(redisTemplate.execute(callbackCaptor.capture())).thenReturn(Collections.emptyList());
        when(mockOperations.exec()).thenReturn(Collections.emptyList());

        redisListCacheService.runInOptimisticLock(task, key);

        SessionCallback<?> capturedCallback = callbackCaptor.getValue();
        capturedCallback.execute(mockOperations);

        verify(redisTemplate).execute(any(SessionCallback.class));
        verify(mockOperations).multi();
        verify(task).run();
        verify(mockOperations).exec();
    }

    @Test
    void testRunInOptimisticLock_ExceptionHandling() {
        when(redisTemplate.execute(callbackCaptor.capture())).thenThrow(new RedisTransactionException());

        assertThrows(RedisTransactionException.class,
                () -> redisListCacheService.runInOptimisticLock(task, "testKey"));

        SessionCallback<?> capturedCallback = callbackCaptor.getValue();
        doThrow(new RuntimeException("Test Exception")).when(mockOperations).exec();

        try {
            capturedCallback.execute(mockOperations);
        } catch (RuntimeException ignored) {
            assertTrue(true);
        } finally {
            verify(mockOperations).discard();
        }
    }

    @Test
    void testLeftPop_WhenValueExists() {
        Object expectedObject = new Object();
        when(redisTemplate.opsForList().leftPop(key)).thenReturn(value);
        when(objectMapper.convertValue(value, Object.class)).thenReturn(expectedObject);

        Optional<Object> result = redisListCacheService.leftPop(key, Object.class);

        assertTrue(result.isPresent());
        assertEquals(expectedObject, result.get());
        verify(redisTemplate.opsForList()).leftPop(key);
        verify(objectMapper).convertValue(value, Object.class);
    }

    @Test
    void testLeftPop_WhenValueDoesNotExist() {
        when(redisTemplate.opsForList()).thenReturn(listOperations);
        when(listOperations.leftPop(key)).thenReturn(null);

        Optional<Object> result = redisListCacheService.leftPop(key, Object.class);

        assertTrue(result.isEmpty());
        verify(redisTemplate.opsForList()).leftPop(key);
        verifyNoInteractions(objectMapper);
    }
}