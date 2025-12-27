package faang.school.postservice.cache;

import faang.school.postservice.cache.model.author.AuthorCache;
import faang.school.postservice.cache.repository.AuthorCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthorCacheRepositoryTest {

    private final String collection = "users";
    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private HashOperations hashOperations;

    @InjectMocks
    private AuthorCacheRepository repository;

    @BeforeEach
    void setUp() {
        repository = new AuthorCacheRepository(redisTemplate);
        ReflectionTestUtils.setField(repository, "collection", collection);
        ReflectionTestUtils.setField(repository, "ttlDays", 7);

        when(redisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testSave_ShouldStoreAuthorInRedisWithTtl() {
        AuthorCache author = AuthorCache.builder()
                .userId(123L)
                .username("test_user")
                .build();

        repository.save(author);

        String expectedKey = collection + "123";
        Map<String, String> expectedData = Map.of(
                "userId", "123",
                "username", "test_user"
        );

        verify(hashOperations).putAll(expectedKey, expectedData);
        verify(redisTemplate).expire(expectedKey, 7, TimeUnit.DAYS);
    }

    @Test
    void testGet_WhenAuthorExists_ShouldReturnAuthor() {
        String key = collection + "123";
        Map<String, String> cachedData = Map.of(
                "userId", "123",
                "username", "test_user"
        );

        when(hashOperations.entries(key)).thenReturn(cachedData);

        Optional<AuthorCache> result = repository.get(123L);

        assertTrue(result.isPresent());
        assertEquals(123L, result.get().userId());
        assertEquals("test_user", result.get().username());
    }

    @Test
    void testGet_WhenAuthorNotExists_ShouldReturnEmpty() {
        String key = collection + "999";
        when(hashOperations.entries(key)).thenReturn(Collections.emptyMap());

        Optional<AuthorCache> result = repository.get(999L);

        assertFalse(result.isPresent());
    }

    @Test
    void testGet_WhenInvalidData_ShouldReturnEmptyAndLogError() {
        String key = collection + "123";
        Map<String, String> invalidData = Map.of(
                "userId", "not_a_number"
        );

        when(hashOperations.entries(key)).thenReturn(invalidData);

        Optional<AuthorCache> result = repository.get(123L);

        assertFalse(result.isPresent());

    }
}
