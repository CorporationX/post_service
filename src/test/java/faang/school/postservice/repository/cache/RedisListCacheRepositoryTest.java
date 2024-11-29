package faang.school.postservice.repository.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisListCacheRepositoryTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOperations;

    @InjectMocks
    private RedisListCacheRepository<Object> redisListCacheRepository;
    private String listKey;
    private Object value;

    @BeforeEach
    void setUp() {
        listKey = "testList";
        value = new Object();

        lenient().when(redisTemplate.opsForList()).thenReturn(listOperations);
    }

    @Test
    void rightPush_shouldPushValueToList() {
        redisListCacheRepository.rightPush(listKey, value);

        verify(listOperations).rightPush(listKey, value);
    }

    @Test
    void get_shouldReturnListOfValues() {
        List<Object> correctResult = List.of(new Object(), new Object());
        when(listOperations.range(listKey, 0, -1)).thenReturn(correctResult);

        List<Object> result = redisListCacheRepository.get(listKey);

        verify(listOperations).range(listKey, 0, -1);
        assertEquals(correctResult, result);
    }
}
