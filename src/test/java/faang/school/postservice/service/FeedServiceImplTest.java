package faang.school.postservice.service;

import faang.school.postservice.config.FeedRedisProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {

    @Mock
    private StringRedisTemplate redis;
    @Mock
    private FeedRedisProperties props;
    @Mock
    private ZSetOperations<String, String> zsetOps;

    private FeedServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new FeedServiceImpl(redis, props);
    }

    @Test
    @DisplayName("addPostToFeed should add post to Redis feed when post is new")
    void addPostToFeed_shouldAdd_whenNew() {

        when(props.getKeyPrefix()).thenReturn("feed:");
        when(props.getMaxSize()).thenReturn(1000);

        long followerId = 10L;
        long postId = 777L;
        Instant occurredAt = Instant.parse("2025-12-28T00:00:00Z");

        when(redis.execute(any(), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(1L);

        service.addPostToFeed(followerId, postId, occurredAt);

        verify(redis).execute(
                any(),
                eq(List.of("feed:" + followerId)),
                anyString(),
                eq(String.valueOf(postId)),
                eq("1000")
        );
    }

    @Test
    @DisplayName("addPostToFeed should ignore duplicate post when Lua script returns 0")
    void addPostToFeed_shouldIgnoreDuplicate() {

        when(props.getKeyPrefix()).thenReturn("feed:");
        when(props.getMaxSize()).thenReturn(1000);

        when(redis.execute(any(), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(0L);

        service.addPostToFeed(1L, 2L, Instant.now());

        verify(redis).execute(any(), anyList(), anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("addPostToFeed should calculate score using occurredAt timestamp and postId")
    void addPostToFeed_shouldCalculateScoreCorrectly() {

        when(props.getKeyPrefix()).thenReturn("feed:");
        when(props.getMaxSize()).thenReturn(1000);

        long followerId = 5L;
        long postId = 1234L; // %1000 = 234
        Instant occurredAt = Instant.ofEpochMilli(1_700_000_000_000L);

        when(redis.execute(any(), anyList(), anyString(), anyString(), anyString()))
                .thenReturn(1L);

        ArgumentCaptor<String> scoreCaptor = ArgumentCaptor.forClass(String.class);

        service.addPostToFeed(followerId, postId, occurredAt);

        verify(redis).execute(
                any(),
                eq(List.of("feed:" + followerId)),
                scoreCaptor.capture(),
                eq(String.valueOf(postId)),
                eq("1000")
        );

        long expectedScore = occurredAt.toEpochMilli() * 1_000 + (postId % 1_000);
        assertEquals(String.valueOf(expectedScore), scoreCaptor.getValue());
    }

    @Test
    @DisplayName("addPostToFeed should propagate exception when Redis execution fails")
    void addPostToFeed_shouldPropagateException() {

        when(props.getKeyPrefix()).thenReturn("feed:");
        when(props.getMaxSize()).thenReturn(1000);

        when(redis.execute(any(), anyList(), anyString(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Redis down"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.addPostToFeed(1L, 2L, Instant.now())
        );
        assertEquals("Redis down", ex.getMessage());
    }

    @Test
    @DisplayName("getLatestPosts should return latest posts mapped to Long")
    void getLatestPosts_shouldReturnLatestPosts() {

        when(props.getKeyPrefix()).thenReturn("feed:");
        when(redis.opsForZSet()).thenReturn(zsetOps);

        when(zsetOps.reverseRange("feed:42", 0, 2))
                .thenReturn(Set.of("101", "102", "103"));

        List<Long> result = service.getLatestPosts(42L, 3);

        assertEquals(3, result.size());
        verify(zsetOps).reverseRange("feed:42", 0, 2);
    }

    @Test
    @DisplayName("getLatestPosts should return empty list when feed does not exist")
    void getLatestPosts_shouldReturnEmptyList_whenNoFeed() {

        when(props.getKeyPrefix()).thenReturn("feed:");
        when(redis.opsForZSet()).thenReturn(zsetOps);

        when(zsetOps.reverseRange("feed:1", 0, 9))
                .thenReturn(Set.of());

        List<Long> result = service.getLatestPosts(1L, 10);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
