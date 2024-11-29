package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostNewsFeedDto;
import faang.school.postservice.mapper.redis.CachedPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CachedPost;
import faang.school.postservice.publisher.kafka.events.FeedHeatEvent;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FeedServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @Mock
    private CachedPostService cachedPostService;

    @Mock
    private CachedPostMapper cachedPostMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CachedAuthorService cachedAuthorService;

    @Spy
    @InjectMocks
    private FeedService feedService;

    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(feedService, "maxFeedSize", 500);
        ReflectionTestUtils.setField(feedService, "feedPageSize", 20);
        ReflectionTestUtils.setField(feedService, "feedCacheKeyPrefix", "feed:");
        lenient().when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    public void testAddPostToFollowersFeed() {
        Long postId = 1L;
        List<Long> followerIds = Arrays.asList(100L, 101L);
        LocalDateTime publishedAt = LocalDateTime.now();

        when(zSetOperations.add(anyString(), anyLong(), anyDouble())).thenReturn(true);
        when(zSetOperations.removeRange(anyString(), anyLong(), anyLong())).thenReturn(1L);

        feedService.addPostToFollowersFeed(postId, followerIds, publishedAt);

        for (Long followerId : followerIds) {
            String expectedFeedKey = "feed:" + followerId;
            double expectedScore = publishedAt.toInstant(ZoneOffset.UTC).toEpochMilli();

            verify(zSetOperations).add(eq(expectedFeedKey), eq(postId), eq(expectedScore));
            verify(zSetOperations).removeRange(eq(expectedFeedKey), eq(0L), eq(-501L));
        }
    }

    @Test
    public void testGetFeedForUser_FirstPage() {
        Long userId = 100L;
        Long postId = null;
        String feedKey = "feed:" + userId;

        Set<Object> postIds = new LinkedHashSet<>(Arrays.asList(3L, 2L, 1L));

        when(zSetOperations.reverseRange(feedKey, 0, 19)).thenReturn(postIds);

        List<CachedPost> cachedPosts = Arrays.asList(
                CachedPost.builder().id(3L).build(),
                CachedPost.builder().id(2L).build(),
                CachedPost.builder().id(1L).build()
        );

        when(cachedPostService.getCachedPostByIds(Arrays.asList(3L, 2L, 1L))).thenReturn(cachedPosts);

        List<PostNewsFeedDto> expectedDtos = Arrays.asList(
                PostNewsFeedDto.builder().id(3L).build(),
                PostNewsFeedDto.builder().id(2L).build(),
                PostNewsFeedDto.builder().id(1L).build()
        );

        when(cachedPostMapper.toPostsNewsFeedDto(cachedPosts)).thenReturn(expectedDtos);

        List<PostNewsFeedDto> result = feedService.getFeedForUser(userId, postId);

        assertEquals(expectedDtos, result);
    }

    @Test
    public void testGetFeedForUser_NextPage() {
        Long userId = 100L;
        Long lastPostId = 2L;
        String feedKey = "feed:" + userId;

        when(zSetOperations.rank(feedKey, lastPostId)).thenReturn(10L);

        when(zSetOperations.reverseRange(feedKey, 11L, 30L)).thenReturn(new LinkedHashSet<>(Arrays.asList(20L, 19L, 18L)));

        List<CachedPost> cachedPosts = Arrays.asList(
                CachedPost.builder().id(20L).build(),
                CachedPost.builder().id(19L).build(),
                CachedPost.builder().id(18L).build()
        );

        when(cachedPostService.getCachedPostByIds(Arrays.asList(20L, 19L, 18L))).thenReturn(cachedPosts);

        List<PostNewsFeedDto> expectedDtos = Arrays.asList(
                PostNewsFeedDto.builder().id(20L).build(),
                PostNewsFeedDto.builder().id(19L).build(),
                PostNewsFeedDto.builder().id(18L).build()
        );

        when(cachedPostMapper.toPostsNewsFeedDto(cachedPosts)).thenReturn(expectedDtos);

        List<PostNewsFeedDto> result = feedService.getFeedForUser(userId, lastPostId);

        assertEquals(expectedDtos, result);
    }

    @Test
    public void testAddPostsToUserFeed() {
        Long userId = 100L;
        String feedKey = "feed:" + userId;

        List<Post> posts = Arrays.asList(
                Post.builder().id(1L).publishedAt(LocalDateTime.now().minusDays(1)).build(),
                Post.builder().id(2L).publishedAt(LocalDateTime.now().minusDays(2)).build(),
                Post.builder().id(3L).publishedAt(LocalDateTime.now().minusDays(3)).build()
        );

        Set<ZSetOperations.TypedTuple<Object>> postTuples = new HashSet<>();
        for (Post post : posts) {
            double score = post.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            postTuples.add(new DefaultTypedTuple<>((Object) post.getId(), score));
        }

        when(zSetOperations.zCard(feedKey)).thenReturn(600L);
        when(zSetOperations.add(eq(feedKey), eq(postTuples))).thenReturn(3L);
        when(zSetOperations.removeRange(eq(feedKey), anyLong(), anyLong())).thenReturn(100L);

        feedService.addPostsToUserFeed(userId, posts);

        verify(zSetOperations).add(feedKey, postTuples);
        verify(zSetOperations).zCard(feedKey);
        verify(zSetOperations).removeRange(feedKey, 0, 600L - 500L - 1);
    }

    @Test
    public void testProcessCacheHeatEvent() {
        FeedHeatEvent event = new FeedHeatEvent(Arrays.asList(100L, 101L));

        when(userServiceClient.getFollowees(anyLong())).thenReturn(Arrays.asList(10L, 11L));
        when(postRepository.findLatestPostsByAuthors(anyList(), any(PageRequest.class))).thenReturn(Collections.emptyList());
        when(cachedPostService.savePostCache(any(Post.class))).thenReturn(null);
        doNothing().when(cachedAuthorService).saveAuthorCache(anyLong());

        feedService.processCacheHeatEvent(event);

        verify(userServiceClient, times(2)).getFollowees(longCaptor.capture());
        List<Long> capturedUserIds = longCaptor.getAllValues();
        assertTrue(capturedUserIds.containsAll(Arrays.asList(100L, 101L)));

        verify(postRepository, times(2)).findLatestPostsByAuthors(anyList(), any(PageRequest.class));
        verify(cachedPostService, times(0)).savePostCache(any(Post.class));
        verify(cachedAuthorService, times(0)).saveAuthorCache(anyLong());
    }

    @Test
    public void testProcessUserFeedHeat() {
        Long userId = 100L;
        List<Long> authorIds = Arrays.asList(10L, 11L);

        when(userServiceClient.getFollowees(userId)).thenReturn(authorIds);

        List<Post> posts = Arrays.asList(
                Post.builder().id(1L).publishedAt(LocalDateTime.now().minusDays(1)).authorId(10L).build(),
                Post.builder().id(2L).publishedAt(LocalDateTime.now().minusDays(2)).authorId(11L).build()
        );

        when(postRepository.findLatestPostsByAuthors(eq(authorIds), any(PageRequest.class))).thenReturn(posts);
        when(cachedPostService.savePostCache(any(Post.class))).thenReturn(null);

        doNothing().when(cachedAuthorService).saveAuthorCache(anyLong());
        doNothing().when(feedService).addPostsToUserFeed(eq(userId), eq(posts));

        ReflectionTestUtils.invokeMethod(feedService, "processUserFeedHeat", userId);

        verify(userServiceClient).getFollowees(userId);
        verify(postRepository).findLatestPostsByAuthors(eq(authorIds), any(PageRequest.class));
        verify(cachedPostService, times(2)).savePostCache(any(Post.class));
        verify(cachedAuthorService, times(2)).saveAuthorCache(anyLong());
        verify(feedService).addPostsToUserFeed(userId, posts);
    }
}