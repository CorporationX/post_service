package faang.school.postservice.util.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostCache;
import faang.school.postservice.dto.post.PostEventDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserCacheRepository;
import faang.school.postservice.service.FeedServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private PostCacheRepository postCacheRepository;

    @Mock
    private UserCacheRepository userCacheRepository;

    @InjectMocks
    private FeedServiceImpl feedService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedService, "maxFeedSize", MAX_FEED_SIZE);
        ReflectionTestUtils.setField(feedService, "feedTtlDays", FEED_TTL_DAYS);
    }

    private PostEventDto createTestEvent(List<Long> followerIds) {
        return new PostEventDto(
                POST_ID,
                "Test post",
                AUTHOR_ID,
                null,
                followerIds,
                LocalDateTime.now(),
                0L,
                0L
                );
    }

    @Test
    void updateFeeds_shouldAddPostToAllFollowers() {
        when(redisTemplate.opsForZSet()).thenReturn(zsetOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        PostEventDto event = createTestEvent(List.of(FOLLOWER_1_ID, FOLLOWER_2_ID, FOLLOWER_3_ID));
        double expectedScore = -event.publishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();

        feedService.updateFeeds(event);

        verify(zsetOperations).add("feed:" + FOLLOWER_1_ID, POST_ID.toString(), expectedScore);
        verify(zsetOperations).add("feed:" + FOLLOWER_2_ID, POST_ID.toString(), expectedScore);
        verify(zsetOperations).add("feed:" + FOLLOWER_3_ID, POST_ID.toString(), expectedScore);

        verify(zsetOperations, times(3)).removeRange(anyString(),
                eq((long) MAX_FEED_SIZE), eq(-1L));
        verify(redisTemplate).expire(eq("feed:" + FOLLOWER_1_ID), any(Duration.class));
        verify(redisTemplate).expire(eq("feed:" + FOLLOWER_2_ID), any(Duration.class));
        verify(redisTemplate).expire(eq("feed:" + FOLLOWER_3_ID), any(Duration.class));
        verify(postCacheRepository).save(any(PostCache.class));
        verify(valueOperations).set(eq("processed:post:" + POST_ID), eq("1"),
                any(Duration.class));
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
        when(postCacheRepository.save(any(PostCache.class)))
                .thenThrow(new RuntimeException("Redis error"));
        PostEventDto event = createTestEvent(List.of(FOLLOWER_1_ID));

        assertThrows(RuntimeException.class, () -> feedService.updateFeeds(event));
    }

    @Test
    void updateFeeds_shouldDecreaseSize() {
        when(redisTemplate.opsForZSet()).thenReturn(zsetOperations);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(redisTemplate.hasKey(anyString())).thenReturn(false);

        PostEventDto event = createTestEvent(List.of(FOLLOWER_1_ID));

        feedService.updateFeeds(event);

        verify(zsetOperations).removeRange("feed:" + FOLLOWER_1_ID, (long) MAX_FEED_SIZE, -1L);
        verify(redisTemplate).expire(eq("feed:" + FOLLOWER_1_ID), any(Duration.class));
    }
}
