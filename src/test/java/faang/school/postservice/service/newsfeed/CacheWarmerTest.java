package faang.school.postservice.service.newsfeed;

import faang.school.postservice.client.SubscriptionClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.properties.FeedProperties;
import faang.school.postservice.dto.newsfeed.KafkaTimePostIdEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheWarmerTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private SubscriptionClient subscriptionClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private RedisCacheService redisCacheService;

    @Mock
    private FeedProperties feedProperties;

    @Spy
    private ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();

    @InjectMocks
    private CacheWarmer cacheWarmer;

    @Captor
    private ArgumentCaptor<KafkaTimePostIdEvent> kafkaTimePostIdEventCaptor;

    @BeforeEach
    void setUp() {
        taskExecutor.initialize();

        lenient().doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            task.run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test
    void warmUpCacheWhenUsersExist() {
        UserDto user1 = new UserDto(1L, "user1", "user1@mail.com");
        UserDto user2 = new UserDto(2L, "user2", "user2@mail.com");
        List<UserDto> usersPage1 = List.of(user1, user2);
        List<UserDto> emptyPage = Collections.emptyList();

        when(feedProperties.getCacheWarmerUserBatchSize()).thenReturn(10);
        when(userServiceClient.getUsersByPage(0, 10)).thenReturn(usersPage1);
        when(userServiceClient.getUsersByPage(1, 10)).thenReturn(emptyPage);

        UserDto followedAuthor1 = new UserDto(10L, "author1", "author1@mail.com");
        when(subscriptionClient.getFollowersByUserId(1L)).thenReturn(List.of(followedAuthor1));
        Post post1 = Post.builder().id(100L).authorId(10L).publishedAt(LocalDateTime.now()).build();
        when(postRepository.findRecentPublishedPostsByAuthorIds(List.of(10L), 100)).thenReturn(List.of(post1));
        when(feedProperties.getMaxFeedSize()).thenReturn(100);

        when(subscriptionClient.getFollowersByUserId(2L)).thenReturn(Collections.emptyList());

        cacheWarmer.warmUpCache();

        verify(redisCacheService).addToFeed(eq(1L), kafkaTimePostIdEventCaptor.capture());
        KafkaTimePostIdEvent eventForUser1 = kafkaTimePostIdEventCaptor.getValue();
        assertEquals(100L, eventForUser1.id());
        assertEquals(post1.getPublishedAt().toInstant(ZoneOffset.UTC).toEpochMilli(), eventForUser1.publishedAt());

        verify(redisCacheService, never()).addToFeed(eq(2L), any());

        verify(userServiceClient, times(2)).getUsersByPage(anyInt(), anyInt());
    }

    @Test
    void warmUpCacheWhenNoUsersToProcess() {
        when(feedProperties.getCacheWarmerUserBatchSize()).thenReturn(10);
        when(userServiceClient.getUsersByPage(0, 10)).thenReturn(Collections.emptyList());

        cacheWarmer.warmUpCache();

        verify(userServiceClient, times(1)).getUsersByPage(0, 10);
        verify(subscriptionClient, never()).getFollowersByUserId(anyLong());
        verify(postRepository, never()).findRecentPublishedPostsByAuthorIds(anyList(), anyInt());
        verify(redisCacheService, never()).addToFeed(anyLong(), any());
    }

    @Test
    void warmUpCacheWhenUserServiceClientThrowsException() {
        when(feedProperties.getCacheWarmerUserBatchSize()).thenReturn(10);
        when(userServiceClient.getUsersByPage(0, 10)).thenThrow(new RuntimeException("User service unavailable"));

        cacheWarmer.warmUpCache();

        verify(userServiceClient, times(1)).getUsersByPage(0, 10);
        verify(subscriptionClient, never()).getFollowersByUserId(anyLong());
    }

    @Test
    void warmUpCacheForUserWhenNoSubscriptions() {
        UserDto user1 = new UserDto(1L, "user1", "user1@mail.com");
        when(feedProperties.getCacheWarmerUserBatchSize()).thenReturn(10);
        when(userServiceClient.getUsersByPage(0, 10)).thenReturn(List.of(user1));
        when(userServiceClient.getUsersByPage(1, 10)).thenReturn(Collections.emptyList());

        when(subscriptionClient.getFollowersByUserId(1L)).thenReturn(Collections.emptyList());

        cacheWarmer.warmUpCache();

        verify(postRepository, never()).findRecentPublishedPostsByAuthorIds(anyList(), anyInt());
        verify(redisCacheService, never()).addToFeed(eq(1L), any());
    }

    @Test
    void warmUpCacheForUserWhenNoRecentPosts() {
        UserDto user1 = new UserDto(1L, "user1", "user1@mail.com");
        UserDto followedAuthor1 = new UserDto(10L, "author1", "author1@mail.com");
        when(feedProperties.getCacheWarmerUserBatchSize()).thenReturn(10);
        when(userServiceClient.getUsersByPage(0, 10)).thenReturn(List.of(user1));
        when(userServiceClient.getUsersByPage(1, 10)).thenReturn(Collections.emptyList());
        when(feedProperties.getMaxFeedSize()).thenReturn(100);

        when(subscriptionClient.getFollowersByUserId(1L)).thenReturn(List.of(followedAuthor1));
        when(postRepository.findRecentPublishedPostsByAuthorIds(List.of(10L), 100)).thenReturn(Collections.emptyList());

        cacheWarmer.warmUpCache();

        verify(redisCacheService, never()).addToFeed(eq(1L), any());
    }

    @Test
    void warmUpCacheForUserWhenPostHasNullPublishedAt() {
        UserDto user1 = new UserDto(1L, "user1", "user1@mail.com");
        UserDto followedAuthor1 = new UserDto(10L, "author1", "author1@mail.com");
        Post postWithNullDate = Post.builder().id(200L).authorId(10L).publishedAt(null).build();
        Post postWithDate = Post.builder().id(201L).authorId(10L).publishedAt(LocalDateTime.now()).build();

        when(feedProperties.getCacheWarmerUserBatchSize()).thenReturn(10);
        when(userServiceClient.getUsersByPage(0, 10)).thenReturn(List.of(user1));
        when(userServiceClient.getUsersByPage(1, 10)).thenReturn(Collections.emptyList());
        when(feedProperties.getMaxFeedSize()).thenReturn(100);

        when(subscriptionClient.getFollowersByUserId(1L)).thenReturn(List.of(followedAuthor1));
        when(postRepository.findRecentPublishedPostsByAuthorIds(List.of(10L), 100))
                .thenReturn(List.of(postWithNullDate, postWithDate));

        cacheWarmer.warmUpCache();

        verify(redisCacheService, times(1)).addToFeed(eq(1L), kafkaTimePostIdEventCaptor.capture());
        KafkaTimePostIdEvent cachedEvent = kafkaTimePostIdEventCaptor.getValue();
        assertEquals(postWithDate.getId(), cachedEvent.id());
    }

    @Test
    void warmUpCacheForUserWhenSubscriptionClientThrows() {
        UserDto user1 = new UserDto(1L, "user1", "user1@mail.com");
        when(feedProperties.getCacheWarmerUserBatchSize()).thenReturn(10);
        when(userServiceClient.getUsersByPage(0, 10)).thenReturn(List.of(user1));
        when(userServiceClient.getUsersByPage(1, 10)).thenReturn(Collections.emptyList());

        when(subscriptionClient.getFollowersByUserId(1L)).thenThrow(new RuntimeException("Subscription service error"));

        cacheWarmer.warmUpCache();

        verify(postRepository, never()).findRecentPublishedPostsByAuthorIds(any(), anyInt());
        verify(redisCacheService, never()).addToFeed(anyLong(), any());
    }
}