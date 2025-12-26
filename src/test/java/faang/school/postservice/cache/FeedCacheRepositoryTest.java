package faang.school.postservice.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
}
