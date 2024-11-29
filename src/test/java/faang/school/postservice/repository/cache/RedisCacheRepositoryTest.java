package faang.school.postservice.repository.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisCacheRepository<Object> redisCacheRepository;

    private String key;
    private Object value;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        key = "key";
        value = new Object();
    }

    @Test
    void set_shouldSetValueInRedis() {
        Duration time = Duration.ofMinutes(10);

        redisCacheRepository.set(key, value, time);

        verify(redisTemplate.opsForValue()).set(key, value, time);
    }

    @Test
    void multiSetIfAbsent_shouldSetMultipleValuesIfAbsent() {
        Map<String, Object> keyByValue = new HashMap<>();
        keyByValue.put("key1", new Object());
        keyByValue.put("key2", new Object());

        redisCacheRepository.multiSetIfAbsent(keyByValue);

        verify(redisTemplate.opsForValue()).multiSetIfAbsent(keyByValue);
    }

    @Test
    void incrementAndGet_shouldIncrementAndReturnCounter() {
        long correctResult = 5L;
        when(redisTemplate.opsForValue().increment(key)).thenReturn(correctResult);

        long result = redisCacheRepository.incrementAndGet(key);

        verify(redisTemplate.opsForValue()).increment(key);
        assertEquals(correctResult, result);
    }

    @Test
    void get_shouldReturnValueAsOptional() {
        when(redisTemplate.opsForValue().get(key)).thenReturn(value);
        when(objectMapper.convertValue(any(Object.class), any(Class.class))).thenReturn(value);

        Optional<Object> result = redisCacheRepository.get(key, Object.class);

        assertTrue(result.isPresent());
        verify(redisTemplate.opsForValue()).get(key);
        verify(objectMapper).convertValue(value, Object.class);
    }

    @Test
    void getAll_shouldReturnListOfValuesAsOptional() {
        List<String> keys = List.of("key1", "key2");
        Object value1 = new Object();
        Object value2 = new Object();
        List<Object> correctResult = List.of(value1, value2);

        when(redisTemplate.opsForValue().multiGet(keys)).thenReturn(correctResult);
        when(objectMapper.convertValue(any(Object.class), any(Class.class))).thenReturn(value1, value2);

        Optional<List<Object>> result = redisCacheRepository.getAll(keys, Object.class);

        assertTrue(result.isPresent());
        assertEquals(correctResult, result.get());
        verify(redisTemplate.opsForValue()).multiGet(keys);
        verify(objectMapper, times(2)).convertValue(any(Object.class), eq(Object.class));
    }
}
