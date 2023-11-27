package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.HeatFeedEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.publisher.HeatPostProducer;
import faang.school.postservice.util.SimplePageImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedHeaterServiceTest {

    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private RedisCacheService redisCacheService;
    @Mock
    private PostService postService;
    @Mock
    private HeatPostProducer heatPostProducer;
    @InjectMocks
    private FeedHeaterService feedHeaterService;

    private SimplePageImpl<UserDto> userDtos;
    private SimplePageImpl<UserDto> emptyUserDtos;

    private UserDto firstUser;
    private UserDto secondUser;

    private Post firstPost;
    private Post secondPost;

    private RedisPost firstRedisPost;
    private RedisPost secondRedisPost;

    private LocalDateTime firstRedisPostPublishedTime;
    private LocalDateTime secondRedisPostPublishedTime;

    private List<Long> userFollowees;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedHeaterService, "usersBatchSize", 1000);
        ReflectionTestUtils.setField(feedHeaterService, "postsBatchSize", 100);
        ReflectionTestUtils.setField(feedHeaterService, "getAllUsersPath", "http://localhost:8080/users");
        userFollowees = new ArrayList<>(List.of(3L, 4L));
        firstRedisPostPublishedTime = LocalDateTime.now().minusDays(1);
        secondRedisPostPublishedTime = LocalDateTime.now().minusDays(2);
        firstPost = Post.builder()
                .id(1L)
                .content("First Post Content")
                .build();
        secondPost = Post.builder()
                .id(2L)
                .content("Second Post Content")
                .build();
        firstRedisPost = RedisPost.builder()
                .postId(1L)
                .content("First Redis Post Content")
                .version(1)
                .publishedAt(firstRedisPostPublishedTime)
                .build();
        secondRedisPost = RedisPost.builder()
                .postId(2L)
                .content("Second Redis Post Content")
                .version(1)
                .publishedAt(secondRedisPostPublishedTime)
                .build();
        firstUser = UserDto.builder()
                .id(1L)
                .username("First User")
                .followeeIds(userFollowees)
                .build();
        secondUser = UserDto.builder()
                .id(2L)
                .username("Second User")
                .followeeIds(userFollowees)
                .build();
        userDtos = new SimplePageImpl<>(List.of(firstUser, secondUser));
        emptyUserDtos = new SimplePageImpl<>(Collections.emptyList());
    }

    @Test
    void heatFeedTest() {
        doReturn(userDtos).doReturn(emptyUserDtos).when(userServiceClient).getAllUsers(any(Pageable.class));
        when(postService.findSortedPostsByAuthorIdsLimit(userFollowees, 100)).thenReturn(List.of(firstPost, secondPost));
        doReturn(firstRedisPost).doReturn(secondRedisPost).when(redisCacheService).updateOrCachePost(any(Post.class));

        feedHeaterService.heatFeed();

        verify(userServiceClient, times(2)).getAllUsers(any(Pageable.class));
        verify(redisCacheService, times(2)).updateOrCacheUser(any(UserDto.class));
        verify(postService, times(2)).findSortedPostsByAuthorIdsLimit(userFollowees, 100);
        verify(redisCacheService, times(4)).updateOrCachePost(any(Post.class));
        verify(heatPostProducer, times(4)).publish(any(HeatFeedEvent.class));
    }
}