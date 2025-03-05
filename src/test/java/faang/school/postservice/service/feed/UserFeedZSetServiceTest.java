package faang.school.postservice.service.feed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserFeedZSetServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private UserFeedZSetService userFeedZSetService;

    @InjectMocks
    private FeedService feedService;

    private static final String FEED_KEY_PREFIX = "feed:";

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    @DisplayName("Add post to feed should add post and trim feed")
    void addPostToFeed_ShouldAddPostAndTrimFeed() {
        Long userId = 1L;
        Long postId = 100L;
        LocalDateTime timestamp = LocalDateTime.now();
        String feedKey = FEED_KEY_PREFIX + userId;
        double score = timestamp.toEpochSecond(ZoneOffset.UTC);
        int feedSize = (int) ReflectionTestUtils.getField(userFeedZSetService, "feed_size");
        when(zSetOperations.addIfAbsent(feedKey, postId.toString(), score)).thenReturn(true);
        userFeedZSetService.addPostToFeed(userId, postId, timestamp);
        verify(zSetOperations).addIfAbsent(feedKey, postId.toString(), score);
        verify(zSetOperations).removeRange(feedKey, 0, -feedSize - 1);
    }

    @Test
    @DisplayName("Get feed posts without last post ID returns latest posts")
    void getFeedPosts_WhenNoLastPostId_ShouldReturnLatestPosts() {
        Long userId = 1L;
        String feedKey = FEED_KEY_PREFIX + userId;
        int pageSize = 10;
        Set<String> mockPosts = new LinkedHashSet<>(Arrays.asList("3", "2", "1"));
        when(zSetOperations.reverseRange(feedKey, 0, pageSize - 1))
                .thenReturn(mockPosts);
        List<Long> result = userFeedZSetService.getFeedPosts(userId, null, pageSize);
        assertEquals(3, result.size());
        assertEquals(Arrays.asList(3L, 2L, 1L), result);
        verify(zSetOperations).reverseRange(feedKey, 0, pageSize - 1);
    }

    @Test
    @DisplayName("Get feed posts with last post ID returns older posts")
    void getFeedPosts_WithLastPostId_ShouldReturnOlderPosts() {
        Long userId = 1L;
        Long lastPostId = 3L;
        String feedKey = FEED_KEY_PREFIX + userId;
        int pageSize = 10;
        double mockScore = 1000.0;
        Set<String> mockPosts = new LinkedHashSet<>(Arrays.asList("2", "1"));
        when(zSetOperations.score(feedKey, lastPostId.toString()))
                .thenReturn(mockScore);
        when(zSetOperations.reverseRangeByScore(
                eq(feedKey),
                eq(Double.NEGATIVE_INFINITY),
                eq(mockScore),
                eq(0L),
                eq((long) pageSize)))
                .thenReturn(mockPosts);
        List<Long> result = userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize);
        assertEquals(2, result.size());
        assertEquals(Arrays.asList(2L, 1L), result);
        verify(zSetOperations).score(feedKey, lastPostId.toString());
        verify(zSetOperations).reverseRangeByScore(
                eq(feedKey),
                eq(Double.NEGATIVE_INFINITY),
                eq(mockScore),
                eq(0L),
                eq((long) pageSize));
    }

    @Test
    @DisplayName("Get feed posts when last post not found returns empty list")
    void getFeedPosts_WhenLastPostNotFound_ShouldReturnEmptyList() {
        Long userId = 1L;
        Long lastPostId = 999L;
        String feedKey = FEED_KEY_PREFIX + userId;
        int pageSize = 10;
        when(zSetOperations.score(feedKey, lastPostId.toString()))
                .thenReturn(null);
        List<Long> result = userFeedZSetService.getFeedPosts(userId, lastPostId, pageSize);
        assertTrue(result.isEmpty());
        verify(zSetOperations).score(feedKey, lastPostId.toString());
    }

    @Test
    @DisplayName("Get feed posts when no posts available returns empty list")
    void getFeedPosts_WhenNoPostsAvailable_ShouldReturnEmptyList() {
        Long userId = 1L;
        String feedKey = FEED_KEY_PREFIX + userId;
        int pageSize = 10;
        when(zSetOperations.reverseRange(feedKey, 0, pageSize - 1))
                .thenReturn(null);
        List<Long> result = userFeedZSetService.getFeedPosts(userId, null, pageSize);
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(zSetOperations).reverseRange(feedKey, 0, pageSize - 1);
    }

    @Test
    @DisplayName("Get feed posts with invalid post ID returns empty list")
    void getFeedPosts_WithInvalidPostId_ShouldReturnEmptyList() {
        Long userId = 1L;
        String feedKey = FEED_KEY_PREFIX + userId;
        int pageSize = 10;
        Set<String> mockPosts = new LinkedHashSet<>(Arrays.asList("not_a_number", "also_not_a_number"));
        when(zSetOperations.reverseRange(feedKey, 0, pageSize - 1))
                .thenReturn(mockPosts);
        List<Long> result = userFeedZSetService.getFeedPosts(userId, null, pageSize);
        assertTrue(result.isEmpty());
        verify(zSetOperations).reverseRange(feedKey, 0, pageSize - 1);
    }

    @Test
    @DisplayName("Add post to feed with duplicate post should still trim feed")
    void addPostToFeed_WhenAddingDuplicatePost_ShouldStillTrimFeed() {
        Long userId = 1L;
        Long postId = 100L;
        LocalDateTime timestamp = LocalDateTime.now();
        String feedKey = FEED_KEY_PREFIX + userId;
        double score = timestamp.toEpochSecond(ZoneOffset.UTC);
        when(zSetOperations.addIfAbsent(feedKey, postId.toString(), score))
                .thenReturn(false);
        userFeedZSetService.addPostToFeed(userId, postId, timestamp);
        verify(zSetOperations).addIfAbsent(feedKey, postId.toString(), score);
        verify(zSetOperations).removeRange(feedKey, 0, -1);
    }
}