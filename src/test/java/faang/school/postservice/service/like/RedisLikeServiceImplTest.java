package faang.school.postservice.service.like;

import faang.school.postservice.exception.LikeOptimisticLockException;
import faang.school.postservice.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Collections;

import static faang.school.postservice.contants.ErrorMessage.ERROR_MAX_ATTEMPTS_EXCEEDED;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NOT_FOUND_POST;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RedisLikeServiceImplTest {
    private static final long POST_ID = 1L;
    private static final String POST_KEY = "post:1";
    private static final int MAX_ATTEMPTS = 3;


    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private HashOperations<String, Object, Object> hashOperations;

    @Mock
    private RedisOperations<String, Object> redisOperations;

    @InjectMocks
    private RedisLikeServiceImpl redisLikeService;

    @BeforeEach
    void setup() {
        lenient().when(stringRedisTemplate.opsForHash()).thenReturn(hashOperations);
    }

    @Test
    void testIncrementLikesForPost_Success() {
        when(stringRedisTemplate.hasKey(POST_KEY)).thenReturn(true);
        when(hashOperations.get(POST_KEY, "version")).thenReturn("1");
        when(stringRedisTemplate.exec()).thenReturn(Collections.singletonList(1L));

        assertDoesNotThrow(() -> redisLikeService.incrementLikesForPost(POST_ID));

        verify(stringRedisTemplate, times(1)).watch(POST_KEY);
        verify(stringRedisTemplate, times(1)).multi();
        verify(stringRedisTemplate, times(1)).exec();
    }

    @Test
    void testIncrementLikesForPost_PostNotFound() {
        when(stringRedisTemplate.hasKey(POST_KEY)).thenReturn(false);

        PostNotFoundException exception = assertThrows(PostNotFoundException.class, () ->
                redisLikeService.incrementLikesForPost(POST_ID));

        assertEquals(ERROR_NOT_FOUND_POST, exception.getMessage());
        verify(stringRedisTemplate, times(1)).unwatch();
        verify(stringRedisTemplate, never()).multi();
    }

    @Test
    void testIncrementLikesForPost_OptimisticLockConflictAndSuccessOnRetry() {
        when(stringRedisTemplate.hasKey(POST_KEY)).thenReturn(true);
        when(hashOperations.get(POST_KEY, "version")).thenReturn("1");

        when(stringRedisTemplate.exec()).thenReturn(null).thenReturn(Collections.singletonList(1L));

        redisLikeService.incrementLikesForPost(POST_ID);

        verify(stringRedisTemplate, times(2)).watch(POST_KEY);
        verify(stringRedisTemplate, times(2)).multi();
        verify(stringRedisTemplate, times(2)).exec();
    }

    @Test
    void testIncrementLikesForPost_OptimisticLockConflictMaxAttempts() {
        when(stringRedisTemplate.hasKey(POST_KEY)).thenReturn(true);
        when(hashOperations.get(POST_KEY, "version")).thenReturn("1");
        when(stringRedisTemplate.exec()).thenReturn(null);

        LikeOptimisticLockException exception = assertThrows(LikeOptimisticLockException.class, () ->
                redisLikeService.incrementLikesForPost(POST_ID));

        assertEquals(String.format(ERROR_MAX_ATTEMPTS_EXCEEDED, POST_ID, MAX_ATTEMPTS), exception.getMessage());

        verify(stringRedisTemplate, times(3)).watch(POST_KEY);
        verify(stringRedisTemplate, times(3)).multi();
        verify(stringRedisTemplate, times(3)).exec();
    }

    @Test
    void testIncrementLikesForPost_ConcurrentVersionUpdate() {
        when(stringRedisTemplate.hasKey(POST_KEY)).thenReturn(true);

        when(hashOperations.get(POST_KEY, "version")).thenReturn("1").thenReturn("2").thenReturn("2");

        when(stringRedisTemplate.exec()).thenReturn(null).thenReturn(Collections.singletonList(1L));

        redisLikeService.incrementLikesForPost(POST_ID);

        verify(hashOperations, times(2)).get(POST_KEY, "version");
        verify(stringRedisTemplate, times(2)).exec();
    }

    @Test
    void testIncrementLikesForPost_InitialVersionZero() {
        when(stringRedisTemplate.hasKey(POST_KEY)).thenReturn(true);
        when(hashOperations.get(POST_KEY, "version")).thenReturn(null, "0");
        when(stringRedisTemplate.exec()).thenReturn(Collections.singletonList(1L));

        redisLikeService.incrementLikesForPost(POST_ID);

        verify(hashOperations).put(POST_KEY, "version", "1");
    }
}