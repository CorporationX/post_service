package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.NewsFeedProperties;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.news.feed.NewsFeed;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.AsyncCacheService;
import faang.school.postservice.service.cache.SingleCacheService;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsFeedServiceImplTest {

    @Mock
    private PostService postService;

    @Mock
    private CommentService commentService;

    @Mock
    private UserService userService;

    @Mock
    private SingleCacheService<Long, Long> viewCacheService;

    @Mock
    private ExecutorService newsFeedThreadPoolExecutor;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private LikeService likeService;

    @Mock
    private PostRepository postRepository;

    @Spy
    private NewsFeedProperties newsFeedProperties;

    @Mock
    private AsyncCacheService<Long, Long> newsFeedAsyncCacheService;

    @InjectMocks
    private NewsFeedServiceImpl newsFeedService;

    private long userId;
    private long firstPostId;
    private List<Long> postIds;
    private List<PostDto> posts;

    @BeforeEach
    void setUp() {
        userId = 1L;
        firstPostId = -1L;
        postIds = Arrays.asList(1L, 2L, 3L);
        posts = Arrays.asList(mock(PostDto.class), mock(PostDto.class), mock(PostDto.class));

        newsFeedProperties.setNewsFeedSize(10);
    }

    @Test
    void testGetNewsFeedBy_userId() {
        when(newsFeedAsyncCacheService.getRange(userId, firstPostId, newsFeedProperties.getNewsFeedSize())).thenReturn(CompletableFuture.completedFuture(postIds));
        when(postService.getPosts(postIds)).thenReturn(posts);

        NewsFeed result = newsFeedService.getNewsFeedBy(userId);

        assertNotNull(result);
        verify(postService).getPosts(postIds);
    }

    @Test
    void testGetPostIdsForNewsFeedBy_cacheHit() {
        List<Long> postIds = Arrays.asList(1L, 2L, 3L);

        when(newsFeedAsyncCacheService.getRange(userId, firstPostId, 10)).thenReturn(CompletableFuture.completedFuture(postIds));

        List<Long> result = newsFeedService.getPostIdsForNewsFeedBy(userId, firstPostId);

        assertEquals(postIds, result);
        verify(newsFeedAsyncCacheService).getRange(userId, firstPostId, 10);
    }

    @Test
    void testGetPostIdsForNewsFeedBy_cacheMiss() {
        List<Long> cachedPostIds = Collections.emptyList();
        List<Long> followerIds = Arrays.asList(1L, 2L, 3L);
        List<Long> fullPostIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
        List<Long> cachePostIds = fullPostIds.subList(3, 5);

        when(newsFeedAsyncCacheService.getRange(userId, firstPostId, 10)).thenReturn(CompletableFuture.completedFuture(cachedPostIds));
        when(userServiceClient.getFollowingIds(userId)).thenReturn(followerIds);
        when(postRepository.findIdsForNewsFeed(followerIds, 10)).thenReturn(fullPostIds);

        newsFeedService.getPostIdsForNewsFeedBy(userId, firstPostId);

        verify(newsFeedAsyncCacheService).getRange(userId, firstPostId, 10);
        verify(postRepository).findIdsForNewsFeed(followerIds, 10);
        verify(newsFeedAsyncCacheService).save(userId, 4L);
        verify(newsFeedAsyncCacheService).save(userId, 5L);
    }

    @Test
    void testPreparePost_called_from_getNewsFeedBy() {
        PostDto postDto = mock(PostDto.class);
        Long postId = 1L;

        UserDto userDto = mock(UserDto.class);
        List<LikeDto> likeDtos = Arrays.asList(mock(LikeDto.class), mock(LikeDto.class));
        List<CommentDto> commentDtos = Arrays.asList(mock(CommentDto.class), mock(CommentDto.class));
        Long viewsCount = 100L;

        when(postDto.getId()).thenReturn(postId);
        when(userService.getUserFromCacheOrService(postDto.getAuthorId())).thenReturn(userDto);
        when(likeService.getLikesForPublishedPostFromCacheOrDb(postId)).thenReturn(likeDtos);
        when(commentService.getCommentsByPostId(postId, 10)).thenReturn(commentDtos);
        when(viewCacheService.get(postId)).thenReturn(viewsCount);

        List<PostDto> posts = Arrays.asList(postDto);

        when(postService.getPosts(postIds)).thenReturn(posts);

        newsFeedService.getNewsFeedBy(userId);

        verify(postDto).setAuthor(userDto);
        verify(postDto).setLikes(likeDtos);
        verify(postDto).setComments(commentDtos);
        verify(postDto).setViewsCount(viewsCount);
    }
}
