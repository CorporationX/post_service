package faang.school.postservice.repository;

import faang.school.postservice.dto.post.RedisPostDto;
import faang.school.postservice.model.Post;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostCacheRepositoryTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    private RedisPostDto post;

    @InjectMocks
    PostCacheRepository postCacheRepository;

    @BeforeEach
    void setup() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

       post = new RedisPostDto(
                1L,
                "Test content",
                101L,
                202L,
                10,
                List.of(),
                LocalDateTime.now(),
                LocalDateTime.now(),
                Set.of(1L, 2L)
        );
    }


    @Test
    public void testSavePostToCache() {
        Duration ttlDuration = Duration.ofDays(1);
        ReflectionTestUtils.setField(postCacheRepository, "ttlDuration", ttlDuration);
        String expectedKey = "post:1";

        postCacheRepository.savePostToCache(post);

        verify(valueOperations, times(1)).set(expectedKey, post, ttlDuration);
    }

    @Test
    void getPostFromCacheShouldReturnPost() {
        String postId = "1";
        String key = "post:1";

        Mockito.when(valueOperations.get(key)).thenReturn(post);

        RedisPostDto result = postCacheRepository.getPostFromCache(postId);


        assertNotNull(result);
        assertEquals(post, result);
        verify(valueOperations, times(1)).get(key);
    }

    @Test
    void getPostFromCacheShouldReturnNullIfNotFound() {
        String postId = "2";
        String key = "post:2";

        Mockito.when(valueOperations.get(key)).thenReturn(null);

        RedisPostDto result = postCacheRepository.getPostFromCache(postId);

        assertNull(result);
        verify(valueOperations, times(1)).get(key);
    }
}