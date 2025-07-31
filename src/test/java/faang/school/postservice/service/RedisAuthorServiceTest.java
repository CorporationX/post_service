package faang.school.postservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.lang.reflect.Field;
import java.time.Duration;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisAuthorServiceTest {

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private ValueOperations<String, Long> valueOperations;

    @Test
    void shouldCacheAuthorWithTTL() throws Exception {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        RedisAuthorService redisAuthorService = new RedisAuthorService(redisTemplate);

        Field ttlField = RedisAuthorService.class.getDeclaredField("authorTtl");
        ttlField.setAccessible(true);
        ttlField.set(redisAuthorService, Duration.ofHours(24));

        Long authorId = 123L;
        redisAuthorService.cacheAuthor(authorId);
        verify(valueOperations).set("author:" + authorId, authorId, Duration.ofHours(24));
    }
}
