package faang.school.postservice.cache;

import faang.school.postservice.cache.repository.FeedCacheRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedCacheRepositoryTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private FeedCacheRepository repository;

    @BeforeEach
    void setUp() {
        repository = new FeedCacheRepository(redisTemplate);
        ReflectionTestUtils.setField(repository, "collection", "feed");
        ReflectionTestUtils.setField(repository, "maxSize", 500);
        ReflectionTestUtils.setField(repository, "ttlDays", 7);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    void testSaveShouldAddPostAndSetExpiry() {
        Long followerId = 123L;
        Long postId = 456L;
        LocalDateTime createdAt = LocalDateTime.now();
        double expectedScore = createdAt.toEpochSecond(ZoneOffset.UTC);
        String expectedKey = "feed123";

        when(zSetOperations.add(expectedKey, postId.toString(), expectedScore)).thenReturn(true);
        when(zSetOperations.size(expectedKey)).thenReturn(1L);

        repository.save(followerId, postId, createdAt);

        verify(zSetOperations).add(expectedKey, postId.toString(), expectedScore);
        verify(redisTemplate).expire(expectedKey, 7, TimeUnit.DAYS);
    }

    @Test
    void testSaveShouldTrimWhenExceedsMaxSize() {
        Long followerId = 123L;
        String expectedKey = "feed123";

        when(zSetOperations.add(eq(expectedKey), anyString(), anyDouble())).thenReturn(true);
        when(zSetOperations.size(expectedKey)).thenReturn(501L);

        repository.save(followerId, 456L, LocalDateTime.now());

        verify(zSetOperations).removeRange(expectedKey, 0, 0);
    }

    @Test
    void testGet_WhenLastPostIdIsNull_ShouldReturnLatestPosts() {
        Long followerId = 123L;
        String expectedKey = "feed123";
        Set<String> expectedIds = Set.of("100", "99", "98");

        when(zSetOperations.reverseRange(expectedKey, 0, 19))
                .thenReturn(expectedIds);

        List<Long> result = repository.get(followerId, null, 20);

        Assertions.assertTrue(result.containsAll(List.of(100L, 99L, 98L)));
        verify(zSetOperations).reverseRange(expectedKey, 0, 19);
        verify(zSetOperations, never()).score(Mockito.any(), Mockito.any());
    }

    @Test
    void testGet_WhenLastPostIdNotFound_ShouldFallbackToLatest() {
        Long followerId = 123L;
        Long lastPostId = 999L;
        String expectedKey = "feed123";
        Set<String> fallbackIds = Set.of("100", "99");

        when(zSetOperations.score(expectedKey, "999")).thenReturn(null);
        when(zSetOperations.reverseRange(expectedKey, 0, 19))
                .thenReturn(fallbackIds);


        List<Long> result = repository.get(followerId, lastPostId, 20);

        Assertions.assertTrue(result.containsAll(List.of(100L, 99L)));
        verify(zSetOperations).score(expectedKey, "999");
        verify(zSetOperations).reverseRange(expectedKey, 0, 19);
    }
}
