package faang.school.postservice.service.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.CacheWarmupTask;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.factory.UserCacheFactory;
import faang.school.postservice.factory.post.FeedPostFactory;
import faang.school.postservice.factory.post.PostCacheFactory;
import faang.school.postservice.kafka.producer.feed.CacheWarmupProducer;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.repository.CacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.SubscriptionRepository;
import faang.school.postservice.repository.redis.feed.FeedCacheRepository;
import faang.school.postservice.repository.redis.post.PostCacheRepository;
import faang.school.postservice.repository.redis.user.UserCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private FeedCacheRepository feedCacheRepository;
    @Mock
    private SubscriptionRepository subscriptionRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostCacheRepository postCacheRepository;
    @Mock
    private PostCacheFactory postCacheFactory;
    @Mock
    private FeedPostFactory feedPostFactory;
    @Mock
    private CacheWarmupProducer cacheWarmupProducer;
    @Mock
    private CacheRepository cacheRepository;
    @Mock
    private UserCacheRepository userCacheRepository;
    @Mock
    private UserCacheFactory userCacheFactory;
    @Mock
    private UserContext userContext;

    @InjectMocks
    private FeedService feedService;

    private final int warmUpBatchSize = 2;
    private final int feedSize = 5;
    private final int feedPageSize = 2;

    @BeforeEach
    void beforeEach() {
        ReflectionTestUtils.setField(feedService, "warmUpBatchSize", warmUpBatchSize);
        ReflectionTestUtils.setField(feedService, "feedSize", feedSize);
        ReflectionTestUtils.setField(feedService, "feedPageSize", feedPageSize);

    }

    @Test
    void updateUserFeeds_shouldAddPostToEachSubscriberFeed() {
        long postId = 42L;
        List<Long> subscribers = List.of(1L, 2L, 3L);
        Post post = Post.builder().id(postId).build();
        PostPublishedEvent event = new PostPublishedEvent();
        event.setPostId(postId);
        event.setSubscribers(subscribers);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        feedService.updateUserFeeds(event);

        for (Long subscriberId : subscribers) {
            verify(feedCacheRepository).addPostToFeed(subscriberId, post);
        }
    }

    @Test
    void updateUserFeeds_shouldThrow_whenPostNotFound() {
        long postId = 100L;
        PostPublishedEvent event = new PostPublishedEvent();
        event.setPostId(postId);
        event.setSubscribers(List.of(1L));
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> feedService.updateUserFeeds(event));

        verify(feedCacheRepository, never()).addPostToFeed(any(), any());
    }

    @Test
    void queueCacheWarmUp_shouldPublishBatchesCorrectly() {
        List<Long> allFollowerIds = List.of(10L, 20L, 30L, 40L, 50L);
        when(subscriptionRepository.getAllFollowerIds()).thenReturn(allFollowerIds);

        feedService.queueCacheWarmUp();

        ArgumentCaptor<CacheWarmupTask> captor = ArgumentCaptor.forClass(CacheWarmupTask.class);
        verify(cacheWarmupProducer, times(3)).publishCacheWarmupTaskEvent(captor.capture());

        List<CacheWarmupTask> tasks = captor.getAllValues();

        assertEquals(List.of(10L, 20L), tasks.get(0).subscriberIds());
        assertEquals(List.of(30L, 40L), tasks.get(1).subscriberIds());
        assertEquals(List.of(50L), tasks.get(2).subscriberIds());
    }

    @Test
    void queueCacheWarmUp_shouldDoNothingWhenNoFollowers() {
        when(subscriptionRepository.getAllFollowerIds()).thenReturn(List.of());

        feedService.queueCacheWarmUp();

        verify(cacheWarmupProducer, never()).publishCacheWarmupTaskEvent(any());
    }

    @Test
    void warmUp_shouldBuildFeedAndUpdateCaches() {
        List<Long> subscriberIds = List.of(1L, 2L);
        List<Post> postsUser1 = List.of(mock(Post.class));
        List<Post> postsUser2 = List.of(mock(Post.class), mock(Post.class));
        when(postRepository.getFeedForUser(1L, feedSize, null)).thenReturn(postsUser1);
        when(postRepository.getFeedForUser(2L, feedSize, null)).thenReturn(postsUser2);
        doNothing().when(feedCacheRepository).addPostsBatched(eq(1L), eq(postsUser1));
        doNothing().when(feedCacheRepository).addPostsBatched(eq(2L), eq(postsUser2));

        FeedService spyService = Mockito.spy(feedService);
        doNothing().when(spyService).updateUserAndPostCaches(any());

        spyService.warmUp(subscriberIds);

        verify(postRepository).getFeedForUser(1L, feedSize, null);
        verify(postRepository).getFeedForUser(2L, feedSize, null);

        verify(feedCacheRepository).addPostsBatched(1L, postsUser1);
        verify(feedCacheRepository).addPostsBatched(2L, postsUser2);

        verify(spyService).updateUserAndPostCaches(postsUser1);
        verify(spyService).updateUserAndPostCaches(postsUser2);
    }

    @Test
    void warmUp_shouldHandleEmptyPostList() {
        List<Long> subscriberIds = List.of(9L);
        when(postRepository.getFeedForUser(9L, feedSize, null)).thenReturn(List.of());

        FeedService spyService = Mockito.spy(feedService);
        doNothing().when(spyService).updateUserAndPostCaches(any());

        spyService.warmUp(subscriberIds);

        verify(feedCacheRepository).addPostsBatched(9L, List.of());
        verify(spyService).updateUserAndPostCaches(List.of());
    }

    @Test
    void getFeed_allPostsFromCache() {
        long userId = 42L;
        List<Long> postIds = List.of(1L, 2L, 3L);
        List<PostCache> postCaches = List.of(new PostCache(), new PostCache(), new PostCache());
        List<FeedPostDto> expectedDtos = List.of(
                new FeedPostDto(), new FeedPostDto(), new FeedPostDto()
        );
        expectedDtos.get(0).setId(1L);
        expectedDtos.get(1).setId(2L);
        expectedDtos.get(2).setId(3L);

        when(userContext.getUserId()).thenReturn(userId);
        when(feedCacheRepository.getFeedAfter(userId, null, feedPageSize)).thenReturn(postIds);
        when(postCacheRepository.getMany(postIds)).thenReturn(postCaches);
        when(feedPostFactory.fromPostCache(any()))
                .thenReturn(expectedDtos.get(0), expectedDtos.get(1), expectedDtos.get(2));


        List<FeedPostDto> result = feedService.getFeed(null);

        assertThat(result).containsExactlyElementsOf(expectedDtos);
        verify(postRepository, never()).findById(any());
    }

    @Test
    void getFeed_someRedisMisses() {
        long userId = 42L;
        List<Long> postIds = List.of(1L, 2L);
        PostCache cachedPost = new PostCache();
        Post dbPost = new Post();
        FeedPostDto cachedDto = new FeedPostDto();
        cachedDto.setId(1L);
        FeedPostDto dbDto = new FeedPostDto();
        dbDto.setId(2L);


        when(userContext.getUserId()).thenReturn(userId);
        when(feedCacheRepository.getFeedAfter(userId, null, feedPageSize)).thenReturn(postIds);
        when(postCacheRepository.getMany(postIds))
                .thenReturn(new ArrayList<>(Arrays.asList(cachedPost, null)));
        when(feedPostFactory.fromPostCache(cachedPost)).thenReturn(cachedDto);
        when(postRepository.findById(2L)).thenReturn(Optional.of(dbPost));
        when(postCacheFactory.fromPost(dbPost)).thenReturn(new PostCache());
        when(postCacheRepository.save(any())).thenReturn(null);
        when(feedPostFactory.fromPost(dbPost)).thenReturn(dbDto);

        List<FeedPostDto> result = feedService.getFeed(null);

        assertThat(result).containsExactly(cachedDto, dbDto);
    }

    @Test
    void getFeed_extendsFromDbWhenFeedShort() {
        long userId = 42L;
        List<Long> postIds = List.of(1L);
        PostCache cachedPost = new PostCache();
        FeedPostDto cachedDto = new FeedPostDto();
        cachedDto.setId(1L);

        Post extraPost = new Post();
        FeedPostDto extraDto = new FeedPostDto();
        extraDto.setId(2L);

        when(userContext.getUserId()).thenReturn(userId);
        when(feedCacheRepository.getFeedAfter(userId, null, feedPageSize)).thenReturn(postIds);
        when(postCacheRepository.getMany(postIds)).thenReturn(List.of(cachedPost));
        when(feedPostFactory.fromPostCache(cachedPost)).thenReturn(cachedDto);
        when(postRepository.getFeedForUser(userId, feedPageSize -1, 1L)).thenReturn(List.of(extraPost));
        when(feedPostFactory.fromPost(extraPost)).thenReturn(extraDto);
        when(postCacheFactory.fromPost(extraPost)).thenReturn(new PostCache());

        List<FeedPostDto> result = feedService.getFeed(null);

        assertThat(result).containsExactly(cachedDto, extraDto);
    }
}
