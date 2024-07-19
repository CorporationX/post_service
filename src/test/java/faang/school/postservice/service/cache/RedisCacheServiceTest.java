package faang.school.postservice.service.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @InjectMocks
    private RedisCacheService redisCacheService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisCacheService, "ttl", 1);
        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void saveToCache_successful() {
        String pattern = "testPattern";
        Long key = 1L;
        Object value = new Object();
        String jsonValue = "{}";

        try {
            when(objectMapper.writeValueAsString(value)).thenReturn(jsonValue);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        redisCacheService.saveToCache(pattern, key, value);

        verify(hashOperations, times(1)).put(pattern, key, jsonValue);
        verify(redisTemplate, times(1)).expire(pattern, 1, TimeUnit.DAYS);
    }
}
