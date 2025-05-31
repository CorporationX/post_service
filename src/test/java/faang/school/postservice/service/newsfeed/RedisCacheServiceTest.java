package faang.school.postservice.service.newsfeed;

import faang.school.postservice.config.properties.FeedProperties;
import faang.school.postservice.dto.newsfeed.KafkaCommentEvent;
import faang.school.postservice.dto.newsfeed.KafkaLikeEvent;
import faang.school.postservice.dto.newsfeed.KafkaPostViewEvent;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.exception.RedisCacheException;
import faang.school.postservice.model.redis.RedisKeyConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.core.HyperLogLogOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RedisCacheServiceTest {

    @Mock
    private RedisTemplate<String, Object> feedRedisTemplate;

    @Mock
    private FeedProperties feedProperties;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private SetOperations<String, Object> setOperations;

    @Mock
    private HyperLogLogOperations<String, Object> hyperLogLogOperations;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private ValueOperations<String, Object> valueOperations;


    @InjectMocks
    private RedisCacheService redisCacheService;

    private final Long userId = 1L;
    private final KafkaTimePostIdEvent timeEvent = KafkaTimePostIdEvent.builder()
            .id(100L)
            .publishedAt(System.currentTimeMillis())
            .build();
    private final KafkaLikeEvent likeEvent = KafkaLikeEvent.builder()
            .postId(200L)
            .userId(2L)
            .authorId(1L)
            .timestamp(System.currentTimeMillis())
            .build();
    private final KafkaPostViewEvent viewEvent = KafkaPostViewEvent.builder()
            .postId(300L).userId(3L).authorId(1L)
            .viewTimestamp(System.currentTimeMillis())
            .build();
    private final KafkaCommentEvent commentEvent = KafkaCommentEvent.builder()
            .commentId(1L)
            .postId(400L)
            .userId(4L)
            .authorId(1L)
            .content("Test comment")
            .createdAt(LocalDateTime.now())
            .build();
    private final UUID eventId = UUID.randomUUID();

    private final int maxFeedSize = 1000;
    private final long feedTtl = 86400L;
    private final long postTtl = 259200L;
    private final int maxComments = 50;
    private final long eventIdTtl = 3600L;

    @BeforeEach
    void setUp() {
        lenient().when(feedRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
        lenient().when(feedRedisTemplate.opsForSet()).thenReturn(setOperations);
        lenient().when(feedRedisTemplate.opsForHyperLogLog()).thenReturn(hyperLogLogOperations);
        lenient().when(feedRedisTemplate.opsForList()).thenReturn(listOperations);
        lenient().when(feedRedisTemplate.opsForValue()).thenReturn(valueOperations);

        lenient().when(feedProperties.getMaxFeedSize()).thenReturn(maxFeedSize);
        lenient().when(feedProperties.getFeedTtl()).thenReturn(feedTtl);
        lenient().when(feedProperties.getPostTtl()).thenReturn(postTtl);
        lenient().when(feedProperties.getMaxComments()).thenReturn(maxComments);
        lenient().when(feedProperties.getEventIdTtl()).thenReturn(eventIdTtl);
    }

    @Test
    void addToFeedSuccessfully() {
        String expectedKey = RedisKeyConstants.FEED_KEY_PREFIX.getValue() + userId;
        redisCacheService.addToFeed(userId, timeEvent);
        verify(zSetOperations).add(expectedKey, timeEvent.id(), timeEvent.publishedAt());
        verify(zSetOperations).removeRange(expectedKey, 0, -maxFeedSize - 1);
        verify(feedRedisTemplate).expire(expectedKey, feedTtl, TimeUnit.SECONDS);
    }

    @Test
    void addToFeedRedisError() {
        doThrow(new RedisSystemException("Redis down", new Throwable()))
                .when(zSetOperations).add(anyString(), any(), anyDouble());
        RedisCacheException exception = assertThrows(RedisCacheException.class,
                () -> redisCacheService.addToFeed(userId, timeEvent));
        assertEquals("Failed to add to feed for user " + userId, exception.getMessage());
    }

    @Test
    void addLikeToPostSuccessfully() {
        String expectedKey = RedisKeyConstants.POST_KEY_PREFIX.getValue() +
                likeEvent.postId() + RedisKeyConstants.LIKES_SUFFIX.getValue();
        redisCacheService.addLikeToPost(likeEvent);
        verify(setOperations).add(expectedKey, likeEvent.userId());
        verify(feedRedisTemplate).expire(expectedKey, postTtl, TimeUnit.SECONDS);
    }

    @Test
    void addLikeToPostRedisError() {
        doThrow(new RedisSystemException("Redis down", new Throwable())).when(setOperations).add(anyString(), any());
        RedisCacheException exception = assertThrows(RedisCacheException.class,
                () -> redisCacheService.addLikeToPost(likeEvent));
        assertEquals("Failed to add like to post " + likeEvent.postId(), exception.getMessage());
    }

    @Test
    void addViewToPostSuccessfully() {
        String expectedKey = RedisKeyConstants.POST_KEY_PREFIX.getValue() +
                viewEvent.postId() + RedisKeyConstants.VIEWS_SUFFIX.getValue();
        redisCacheService.addViewToPost(viewEvent);
        verify(hyperLogLogOperations).add(expectedKey, viewEvent.userId());
        verify(feedRedisTemplate).expire(expectedKey, postTtl, TimeUnit.SECONDS);
    }

    @Test
    void addViewToPostRedisError() {
        doThrow(new RedisSystemException("Redis down", new Throwable()))
                .when(hyperLogLogOperations).add(anyString(), any());
        RedisCacheException exception = assertThrows(RedisCacheException.class,
                () -> redisCacheService.addViewToPost(viewEvent));
        assertEquals("Failed to add view to post " + viewEvent.postId(), exception.getMessage());
    }

    @Test
    void addCommentToPostSuccessfully() {
        String expectedKey = RedisKeyConstants.POST_KEY_PREFIX.getValue() +
                commentEvent.postId() + RedisKeyConstants.COMMENTS_SUFFIX.getValue();
        redisCacheService.addCommentToPost(commentEvent);
        verify(listOperations).leftPush(expectedKey, commentEvent);
        verify(listOperations).trim(expectedKey, 0, maxComments - 1);
        verify(feedRedisTemplate).expire(expectedKey, postTtl, TimeUnit.SECONDS);
    }

    @Test
    void addCommentToPostRedisError() {
        doThrow(new RedisSystemException("Redis down", new Throwable()))
                .when(listOperations).leftPush(anyString(), any());
        RedisCacheException exception = assertThrows(RedisCacheException.class,
                () -> redisCacheService.addCommentToPost(commentEvent));
        assertEquals("Failed to add comment to post " + commentEvent.postId(), exception.getMessage());
    }

    @Test
    void isAlreadyProcessedReturnsFalse() {
        String expectedKey = RedisKeyConstants.PROCESSED_PREFIX.getValue() + eventId;
        when(valueOperations.setIfAbsent(expectedKey, "1", eventIdTtl, TimeUnit.SECONDS)).thenReturn(true);
        assertFalse(redisCacheService.isAlreadyProcessed(eventId));
        verify(valueOperations).setIfAbsent(expectedKey, "1", eventIdTtl, TimeUnit.SECONDS);
    }

    @Test
    void isAlreadyProcessedReturnsTrue() {
        String expectedKey = RedisKeyConstants.PROCESSED_PREFIX.getValue() + eventId;
        when(valueOperations.setIfAbsent(expectedKey, "1", eventIdTtl, TimeUnit.SECONDS)).thenReturn(false);
        assertTrue(redisCacheService.isAlreadyProcessed(eventId));
    }

    @Test
    void isAlreadyProcessedSetIfAbsent() {
        String expectedKey = RedisKeyConstants.PROCESSED_PREFIX.getValue() + eventId;
        when(valueOperations.setIfAbsent(expectedKey, "1", eventIdTtl, TimeUnit.SECONDS)).thenReturn(null);
        assertFalse(redisCacheService.isAlreadyProcessed(eventId));
    }

    @Test
    void isAlreadyProcessedRedisError() {
        doThrow(new RedisSystemException("Redis down", new Throwable()))
                .when(valueOperations).setIfAbsent(anyString(), anyString(), anyLong(), any(TimeUnit.class));
        RedisCacheException exception = assertThrows(RedisCacheException.class,
                () -> redisCacheService.isAlreadyProcessed(eventId));
        assertEquals("Failed to check processed for event " + eventId, exception.getMessage());
    }

    @Test
    void markAsProcessedSuccessfully() {
        String expectedKey = RedisKeyConstants.PROCESSED_PREFIX.getValue() + eventId;
        redisCacheService.markAsProcessed(eventId);
        verify(valueOperations).set(expectedKey, "1", eventIdTtl, TimeUnit.SECONDS);
    }

    @Test
    void markAsProcessedRedisError() {
        doThrow(new RedisSystemException("Redis down", new Throwable()))
                .when(valueOperations).set(anyString(), anyString(), anyLong(), any(TimeUnit.class));
        RedisCacheException exception = assertThrows(RedisCacheException.class, () -> redisCacheService.markAsProcessed(eventId));
        assertEquals("Failed to mark event as processed: " + eventId, exception.getMessage());
    }
}
