package faang.school.postservice.repository.cache;

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
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class RedisZSetCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    
    @Mock
    private ObjectMapper objectMapper;
    
    @InjectMocks
    private RedisZSetCacheRepository<Object> redisZSetCacheRepository;
    
    @Mock
    private ZSetOperations<String, Object> zSetOperations;
    
    @Mock
    private RedisOperations<String, Object> redisOperations;

    @Mock
    private ZSetOperations.TypedTuple<Object> mockTuple;

    @Mock
    private Runnable task;

    @Captor
    private ArgumentCaptor<SessionCallback<Object>> sessionCallbackCaptor;

    private String key;
    private Object value;
    private double score;
    private String startValueKey;
    private int offset;
    private int count;

    @BeforeEach
    void setUp() {
        key = "testKey";
        value = new Object();
        score = 10.5;

        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        startValueKey = "startValue";
        offset = 0;
        count = 5;
    }

    @Test
    void put_shouldAddElementToSortedSet() {
        redisZSetCacheRepository.put(key, value, score);

        verify(zSetOperations).add(key, value, score);
    }

    @Test
    void size_shouldReturnSizeOfSortedSet() {
        long correctResult = 5L;
        when(zSetOperations.size(key)).thenReturn(correctResult);

        long result = redisZSetCacheRepository.size(key);

        verify(zSetOperations).size(key);
        assertEquals(correctResult, result);
    }

    @Test
    void size_shouldReturnZeroIfSortedSetIsNull() {
        long correctResult = 0L;
        when(zSetOperations.size(key)).thenReturn(null);

        long result = redisZSetCacheRepository.size(key);

        verify(zSetOperations).size(key);
        assertEquals(correctResult, result);
    }

    @Test
    void executeInOptimisticLock_shouldCallMethodsInLambda() {
        redisZSetCacheRepository.executeInOptimisticLock(task, key);

        verify(redisTemplate).execute(sessionCallbackCaptor.capture());

        SessionCallback<?> capturedCallback = sessionCallbackCaptor.getValue();
        when(redisOperations.exec()).thenReturn(List.of("OK"));

        capturedCallback.execute(redisOperations);

        verifyOptimisticLock();
        verify(redisOperations, never()).discard();
    }

    @Test
    void executeInOptimisticLock_shouldThrowExceptionIfExecFails() {
        redisZSetCacheRepository.executeInOptimisticLock(task, key);

        verify(redisTemplate).execute(sessionCallbackCaptor.capture());

        SessionCallback<?> capturedCallback = sessionCallbackCaptor.getValue();
        when(redisOperations.exec()).thenReturn(Collections.emptyList());
        assertThrows(RedisTransactionException.class,
                () -> capturedCallback.execute(redisOperations));

        verifyOptimisticLock();
        verify(redisOperations).discard();
    }

    @Test
    void getRange_shouldReturnConvertedValues() {
        double micScore = score + 0.000001;
        Object value1 = new Object();
        Object value2 = new Object();
        List<Object> correctResult = List.of(value2, value1);
        Set<ZSetOperations.TypedTuple<Object>> typedTuples = Set.of(
                createTypedTuple(value1,  score),
                createTypedTuple(value2, score)
        );

        when(zSetOperations.score(key, startValueKey)).thenReturn(score);
        when(zSetOperations.rangeByScoreWithScores(key, micScore, Double.MAX_VALUE, offset, count)).thenReturn(typedTuples);
        when(objectMapper.convertValue(value1, Object.class)).thenReturn(value1);
        when(objectMapper.convertValue(value2, Object.class)).thenReturn(value2);

        List<Object> result = redisZSetCacheRepository.getRange(key, startValueKey, offset, count, Object.class);

        assertEquals(correctResult, result);
        verify(redisTemplate.opsForZSet()).score(key, startValueKey);
        verify(redisTemplate.opsForZSet()).rangeByScoreWithScores(key, micScore, Double.MAX_VALUE, offset, count);
        verify(objectMapper).convertValue(value1, Object.class);
        verify(objectMapper).convertValue(value2, Object.class);
    }

    @Test
    void getRange_shouldReturnEmptyListIfNoResults() {
        when(zSetOperations.score(key, startValueKey)).thenReturn(null);
        when(zSetOperations.rangeByScoreWithScores(key, Double.NEGATIVE_INFINITY, Double.MAX_VALUE, offset, count))
                .thenReturn(null);

        List<Object> result = redisZSetCacheRepository.getRange(key, startValueKey, offset, count, Object.class);

        assertEquals(Collections.emptyList(), result);
        verify(redisTemplate.opsForZSet()).score(key, startValueKey);
        verify(redisTemplate.opsForZSet()).rangeByScoreWithScores(key, Double.NEGATIVE_INFINITY, Double.MAX_VALUE, offset, count);
        verifyNoInteractions(objectMapper);
    }

    @Test
    void get_shouldReturnAllElements() {
        Set<Object> mockSet = Set.of(new Object(), new Object(), new Object());

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.range(key, 0, -1)).thenReturn(mockSet);

        Set<Object> result = redisZSetCacheRepository.get(key);

        assertEquals(mockSet, result);
        verify(redisTemplate.opsForZSet()).range(key, 0, -1);
    }

    @Test
    void popMin_shouldReturnElementWithMinScore() {
        when(mockTuple.getValue()).thenReturn(value);

        when(redisTemplate.opsForZSet().popMin(key)).thenReturn(mockTuple);

        Optional<Object> result = redisZSetCacheRepository.popMin(key);

        assertTrue(result.isPresent());
        assertEquals(value, result.get());
        verify(redisTemplate.opsForZSet()).popMin(key);
    }

    @Test
    void popMin_shouldReturnEmptyOptionalWhenEmpty() {
        when(redisTemplate.opsForZSet().popMin(key)).thenReturn(null);

        Optional<Object> result = redisZSetCacheRepository.popMin(key);

        assertFalse(result.isPresent());
        verify(redisTemplate.opsForZSet()).popMin(key);
    }

    private void verifyOptimisticLock() {
        verify(redisOperations).watch(key);
        verify(redisOperations).multi();
        verify(task).run();
        verify(redisOperations).exec();
        verify(redisOperations).unwatch();
    }

    private ZSetOperations.TypedTuple<Object> createTypedTuple(Object value, double score) {
        return new ZSetOperations.TypedTuple<>() {
            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public Double getScore() {
                return score;
            }

            @Override
            public int compareTo(ZSetOperations.TypedTuple<Object> o) {
                return Double.compare(score, Optional.ofNullable(o.getScore()).orElse(0.0));
            }
        };
    }
}
