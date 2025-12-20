package faang.school.postservice.util.service;

import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.service.FeedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedServiceImplTest {

    private static final Long POST_ID = 15L;
    private static final Long AUTHOR_ID = 10L;
    private static final Long FOLLOWER_1_ID = 1L;
    private static final Long FOLLOWER_2_ID = 2L;
    private static final Long FOLLOWER_3_ID = 3L;

    private static final int MAX_FEED_SIZE = 500;
    private static final int FEED_TTL_DAYS = 30;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zsetOperations;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() {
        feedService = new FeedServiceImpl(redisTemplate);

        ReflectionTestUtils.setField(feedService, "maxFeedSize", MAX_FEED_SIZE);
        ReflectionTestUtils.setField(feedService, "feedTtlDays", FEED_TTL_DAYS);
    }

    private PostEventDto createTestEvent(List<Long> followerIds) {
        return new PostEventDto(
                POST_ID,
                AUTHOR_ID,
                null,
                followerIds,
                LocalDateTime.now()
        );
    }

    @Test
    void updateFeeds_shouldAddPostToAllFollowers() {
        when(redisTemplate.opsForZSet()).thenReturn(zsetOperations);

        PostEventDto event = createTestEvent(List.of(FOLLOWER_1_ID, FOLLOWER_2_ID, FOLLOWER_3_ID));
        double expectedScore = -event.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        feedService.updateFeeds(event);

        verify(zsetOperations).add("feed:" + FOLLOWER_1_ID, POST_ID, expectedScore);
        verify(zsetOperations).add("feed:" + FOLLOWER_2_ID, POST_ID, expectedScore);
        verify(zsetOperations).add("feed:" + FOLLOWER_3_ID, POST_ID, expectedScore);

        verify(zsetOperations, times(3)).removeRange(anyString(),
                eq((long) MAX_FEED_SIZE), eq(-1L));
        verify(redisTemplate, times(3)).expire(anyString(), any(Duration.class));
    }

    @Test
    void updateFeeds_whenNoFollowers_shouldLogAndReturn() {
        PostEventDto event = createTestEvent(List.of());

        feedService.updateFeeds(event);

        verifyNoInteractions(zsetOperations);
        verifyNoInteractions(redisTemplate);
    }

    @Test
    void updateFeeds_whenRedisThrowsException_shouldRethrow() {
        when(redisTemplate.opsForZSet()).thenReturn(zsetOperations);
        when(zsetOperations.add(anyString(), any(), anyDouble()))
                .thenThrow(new RuntimeException("Redis error"));

        PostEventDto event = createTestEvent(List.of(FOLLOWER_1_ID));

        assertThrows(RuntimeException.class, () -> feedService.updateFeeds(event));
    }

    @Test
    void updateFeeds_shouldDecreaseSize() {
        when(redisTemplate.opsForZSet()).thenReturn(zsetOperations);

        PostEventDto event = createTestEvent(List.of(FOLLOWER_1_ID));

        feedService.updateFeeds(event);

        verify(zsetOperations).removeRange("feed:" + FOLLOWER_1_ID, MAX_FEED_SIZE, -1);
    }
}
