package faang.school.postservice.service;

import faang.school.postservice.cache.AuthorCacheService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorCacheServiceTest {

    @Mock
    private RedisTemplate<String, Long> redisTemplate;

    @Mock
    private SetOperations<String, Long> setOperations;

    private AuthorCacheService authorCacheService;

    @BeforeEach
    void setUp() {
        authorCacheService = new AuthorCacheService(redisTemplate);
        ReflectionTestUtils.setField(authorCacheService, "authorsKey", "test_authors");
        ReflectionTestUtils.setField(authorCacheService, "expirationHours", 2);
    }

    @Test
    void testCacheAuthor() {
        Long authorId = 123L;

        when(redisTemplate.opsForSet()).thenReturn(setOperations);

        authorCacheService.cacheAuthor(authorId);

        verify(setOperations).add("test_authors", authorId);
        verify(redisTemplate).expire("test_authors", Duration.ofHours(2));
    }

    @Test
    void testCacheAuthor_nullId() {
        authorCacheService.cacheAuthor(null);

        verifyNoInteractions(redisTemplate);
    }

    @Test
    void testIsAuthorCached_true() {
        Long authorId = 456L;

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.isMember("test_authors", authorId)).thenReturn(true);

        assertTrue(authorCacheService.isAuthorCached(authorId));
    }

    @Test
    void testIsAuthorCached_false() {
        Long authorId = 789L;

        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        when(setOperations.isMember("test_authors", authorId)).thenReturn(false);

        assertFalse(authorCacheService.isAuthorCached(authorId));
    }
}

