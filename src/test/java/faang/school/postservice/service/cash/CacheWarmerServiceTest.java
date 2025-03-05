package faang.school.postservice.service.cash;

import com.google.common.collect.Sets;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostVisibility;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.entities.PostCache;
import faang.school.postservice.redis.entities.UserCache;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.repository.redis.UserCacheRepository;
import faang.school.postservice.service.feed.UserFeedZSetService;
import faang.school.postservice.service.subscription.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CacheWarmerServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostCacheRepository postCacheRepository;
    @Mock
    private UserCacheRepository userCacheRepository;
    @Mock
    private UserFeedZSetService userFeedZSetService;
    @Mock
    private UserContext userContext;
    @Mock
    private SubscriptionService subscriptionService;
    @Mock
    private CommentCacheService commentCacheService;
    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private CacheWarmerService cacheWarmerService;

    private Post testPost;
    private UserDto testUser;
    private Set<Long> activeAuthors;
    private Set<Long> activeProjects;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cacheWarmerService, "batchSize", 100);
        now = LocalDateTime.now();
        testPost = Post.builder()
                .id(1L)
                .authorId(1L)
                .content("Test content")
                .createdAt(now)
                .updatedAt(now)
                .publishedAt(now)
                .published(true)
                .verified(true)
                .visibility(PostVisibility.PUBLIC)
                .likesCount(10L)
                .commentsCount(5L)
                .build();

        testUser = UserDto.builder()
                .id(1L)
                .username("testUser")
                .build();

        activeAuthors = Sets.newHashSet(1L, 2L);
        activeProjects = Sets.newHashSet(3L, 4L);
    }

    @Test
    @DisplayName("Warm up cache should complete without errors")
    void warmUpCache_SuccessfulWarmUp_ShouldCompleteWithoutErrors() {
        CacheWarmerService spyService = spy(cacheWarmerService);
        CompletableFuture<Void> completedTask = CompletableFuture.completedFuture(null);
        doReturn(completedTask).when(spyService).createUsersCachingTask(anyLong(), anySet());
        doReturn(completedTask).when(spyService).createPostsCachingTask(anyLong(), anySet());
        doReturn(completedTask).when(spyService).createFeedsCachingTask(anyLong(), anySet(), anySet());
        when(userContext.getUserId()).thenReturn(1L);
        when(subscriptionService.getAuthorIds()).thenReturn(activeAuthors);
        when(subscriptionService.getProjectIds()).thenReturn(activeProjects);
        spyService.warmUpCache();
        verify(userContext).getUserId();
        verify(subscriptionService).getAuthorIds();
        verify(subscriptionService).getProjectIds();
    }

    @Test
    @DisplayName("Warm up users with valid users should cache all users")
    void warmUpUsers_WithValidUsers_ShouldCacheAllUsers() {
        when(userServiceClient.findById(1L)).thenReturn(Optional.of(testUser));
        when(userServiceClient.findById(2L)).thenReturn(Optional.of(testUser));
        ReflectionTestUtils.invokeMethod(cacheWarmerService, "warmUpUsers", activeAuthors);
        verify(userCacheRepository, times(2)).save(any(UserCache.class));
    }

    @Test
    @DisplayName("Warm up posts with valid posts should cache posts and comments")
    void warmUpPosts_WithValidPosts_ShouldCachePostsAndComments() {
        PostCache mockPostCache = new PostCache();
        when(postMapper.toPostCache(any(Post.class))).thenReturn(mockPostCache);
        when(postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(anyLong()))
                .thenReturn(Collections.singletonList(testPost));
        when(commentCacheService.fetchLatestComments(anyLong()))
                .thenReturn(new LinkedHashSet<>());
        ReflectionTestUtils.invokeMethod(cacheWarmerService, "warmUpPosts", activeAuthors);
        verify(postCacheRepository, times(2)).save(any(PostCache.class));
        verify(commentCacheService, times(2)).fetchLatestComments(anyLong());
    }

    @Test
    @DisplayName("Warm up feeds should process both user and project feeds")
    void warmUpFeeds_ShouldProcessBothUserAndProjectFeeds() {
        when(postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(anyLong()))
                .thenReturn(Collections.singletonList(testPost));
        when(postRepository.findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(anyLong()))
                .thenReturn(Collections.singletonList(testPost));
        when(userServiceClient.getUserSubscribersIds(anyLong()))
                .thenReturn(Arrays.asList(1L, 2L));
        when(userServiceClient.getProjectSubscriptions(anyLong()))
                .thenReturn(Arrays.asList(1L, 2L));
        ReflectionTestUtils.invokeMethod(cacheWarmerService, "warmUpFeeds", activeAuthors, activeProjects);
        verify(userFeedZSetService, atLeast(4)).addPostToFeed(anyLong(), anyLong(), any(LocalDateTime.class));
    }

    @Test
    void createUserCache_ShouldMapAllFields() {
        UserCache result = ReflectionTestUtils.invokeMethod(cacheWarmerService, "createUserCache", testUser);
        assertNotNull(result, "Result should not be null");
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getUsername(), result.getUsername());
    }

    @Test
    void warmUpPosts_ShouldCorrectlyMapPost() {
        PostMapper mockMapper = mock(PostMapper.class);
        PostCache expectedPostCache = PostCache.builder()
                .id(testPost.getId())
                .authorId(testPost.getAuthorId())
                .build();
        when(mockMapper.toPostCache(testPost)).thenReturn(expectedPostCache);
        ReflectionTestUtils.setField(cacheWarmerService, "postMapper", mockMapper);
        when(postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(anyLong()))
                .thenReturn(Collections.singletonList(testPost));
        when(commentCacheService.fetchLatestComments(anyLong()))
                .thenReturn(new LinkedHashSet<>());
        ReflectionTestUtils.invokeMethod(cacheWarmerService, "warmUpPosts", Collections.singleton(1L));
        verify(mockMapper).toPostCache(testPost);
        verify(postCacheRepository).save(expectedPostCache);
    }

    @Test
    void warmUpCache_WhenExceptionOccurs_ShouldLogAndRethrow() {
        when(subscriptionService.getAuthorIds()).thenThrow(new RuntimeException("Test error"));
        assertThrows(RuntimeException.class, () -> cacheWarmerService.warmUpCache());
    }

    @Test
    void warmUpUsers_WhenUserClientFails_ShouldContinueWithOtherUsers() {
        when(userServiceClient.findById(1L)).thenThrow(new RuntimeException("API Error"));
        when(userServiceClient.findById(2L)).thenReturn(Optional.of(testUser));
        ReflectionTestUtils.invokeMethod(cacheWarmerService, "warmUpUsers", activeAuthors);
        verify(userCacheRepository, times(1)).save(any(UserCache.class));
    }

    @Test
    void warmUpPosts_WhenPostRepositoryFails_ShouldContinueWithOtherAuthors() {
        PostCache mockPostCache = new PostCache();
        when(postMapper.toPostCache(any(Post.class))).thenReturn(mockPostCache);
        when(postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(1L))
                .thenThrow(new RuntimeException("DB Error"));
        when(postRepository.findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(2L))
                .thenReturn(Collections.singletonList(testPost));
        when(commentCacheService.fetchLatestComments(anyLong()))
                .thenReturn(new LinkedHashSet<>());
        ReflectionTestUtils.invokeMethod(cacheWarmerService, "warmUpPosts", activeAuthors);
        verify(postCacheRepository, times(1)).save(any(PostCache.class));
    }
}