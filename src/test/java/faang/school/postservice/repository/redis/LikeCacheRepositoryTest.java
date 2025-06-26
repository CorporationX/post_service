package faang.school.postservice.repository.redis;

import faang.school.postservice.exception.PostNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test cases of LikeCacheRepositoryTest")
public class LikeCacheRepositoryTest {

    private static final long POST_ID = 1L;
    private static final long USER_ID = 100L;
    private static final String POST_KEY_PREFIX = "post:";
    private static final String LIKES_COUNT_POSTFIX = ":likesCount";
    private static final String LIKED_USERS_POSTFIX = ":likedUsers";


    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private RedisOperations<String, Object> redisOperations;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @InjectMocks
    private LikeCacheRepository likeCacheRepository;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(likeCacheRepository, "postKeyPrefix", POST_KEY_PREFIX);
        ReflectionTestUtils.setField(likeCacheRepository, "likesCountPostfix", LIKES_COUNT_POSTFIX);
        ReflectionTestUtils.setField(likeCacheRepository, "likedUsersPostfix", LIKED_USERS_POSTFIX);

        when(redisTemplate.execute(any(SessionCallback.class))).thenAnswer(invocation -> {
            SessionCallback<?> callback = invocation.getArgument(0);
            return callback.execute(redisOperations);
        });
    }

    @Test
    @DisplayName("incrementLikesPost - post not found")
    public void testIncrementLikesPostPostNotFound() {
        String expectedErrorMessage = "Post not found in cache. ID: " + POST_ID;
        when(redisOperations.hasKey(anyString())).thenReturn(false);

        Exception actualException = assertThrows(
                PostNotFoundException.class,
                () -> likeCacheRepository.incrementLikesPost(POST_ID, USER_ID)
        );

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    @DisplayName("incrementLikesPost - user already liked")
    public void testIncrementLikesPostUserAlreadyLiked() {
        when(redisOperations.hasKey(anyString())).thenReturn(true);
        when(redisOperations.opsForSet()).thenReturn(setOperations);
        when(setOperations.isMember(anyString(), eq(USER_ID))).thenReturn(true);

        likeCacheRepository.incrementLikesPost(POST_ID, USER_ID);

        verify(redisOperations, never()).multi();
        verify(redisOperations, times(1)).hasKey(anyString());
    }

    @Test
    @DisplayName("incrementLikesPost - optimistic lock")
    public void testIncrementLikesPostOptimisticLock() {
        String expectedErrorMessage = "Concurrent modification error likes count for post " + POST_ID;

        when(redisOperations.hasKey(anyString())).thenReturn(true);
        when(setOperations.isMember(anyString(), any(Long.class))).thenReturn(false);
        when(redisOperations.opsForSet()).thenReturn(setOperations);
        when(redisOperations.opsForValue()).thenReturn(valueOperations);
        when(redisOperations.exec()).thenReturn(null);

        Exception actualException = assertThrows(
                OptimisticLockingFailureException.class,
                () -> likeCacheRepository.incrementLikesPost(POST_ID, USER_ID)
        );

        assertEquals(expectedErrorMessage, actualException.getMessage());
    }

    @Test
    @DisplayName("incrementLikesPost - success")
    public void testIncrementLikesPostSuccess() {
        String postKey = "post:1";
        String likedUsersKey = "post:1:likedUsers";
        String likesCountKey = "post:1:likesCount";

        when(redisOperations.hasKey(postKey)).thenReturn(true);
        when(redisOperations.opsForSet()).thenReturn(setOperations);
        when(redisOperations.opsForValue()).thenReturn(valueOperations);
        when(setOperations.isMember(likedUsersKey, USER_ID)).thenReturn(false);
        when(redisOperations.exec()).thenReturn(Collections.singletonList(1L));

        assertDoesNotThrow(() -> likeCacheRepository.incrementLikesPost(POST_ID, USER_ID));

        verify(redisOperations).multi();
        verify(setOperations).add(likedUsersKey, USER_ID);
        verify(valueOperations).increment(likesCountKey);
        verify(redisOperations).exec();
    }
}
