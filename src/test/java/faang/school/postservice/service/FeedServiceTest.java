package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.redis.TimePostId;
import faang.school.postservice.mapper.redis.RedisPostMapperImpl;
import faang.school.postservice.mapper.redis.RedisUserMapperImpl;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    @Spy
    private RedisUserMapperImpl redisUserMapper;
    @Spy
    private RedisPostMapperImpl redisPostMapper;
    @InjectMocks
    private FeedService feedService;

    private RedisUser redisUser1;
    private RedisUser redisUser2;
    private RedisUser redisUser3;
    private PostDto postDto1;
    private PostDto postDto2;
    private RedisPost redisPost1;
    private RedisPost redisPost2;
    private final LocalDateTime TEST_TIME1 = LocalDateTime.now();
    private final LocalDateTime TEST_TIME2 = LocalDateTime.now().minusDays(5);
    private final Long USER_ID = 1L;

    @BeforeEach
    void initData() {
        ReflectionTestUtils.setField(feedService, "postsBatchSize", 2);
        ReflectionTestUtils.setField(feedService, "feedBatchSize", 2);
        redisUser1 = RedisUser.builder()
                .id(USER_ID)
                .username("user1")
                .followeeIds(List.of(2L, 3L, 4L))
                .followerIds(List.of(5L, 6L))
                .build();
        redisUser2 = RedisUser.builder()
                .id(2L)
                .username("user2")
                .followerIds(List.of(1L))
                .build();
        redisUser3 = RedisUser.builder()
                .id(3L)
                .username("user3")
                .followerIds(List.of(1L))
                .build();

        postDto1 = PostDto.builder()
                .id(1L)
                .content("post1")
                .authorId(2L)
                .publishedAt(TEST_TIME1)
                .build();
        postDto2 = PostDto.builder()
                .id(2L)
                .authorId(3L)
                .content("post2")
                .publishedAt(TEST_TIME2)
                .build();
        redisPost1 = RedisPost.builder()
                .id(1L)
                .content("post1")
                .userId(2L)
                .publishedAt(TEST_TIME1)
                .build();
        redisPost2 = RedisPost.builder()
                .id(2L)
                .content("post2")
                .userId(3L)
                .publishedAt(TEST_TIME2)
                .build();
    }

    @Test
    void testHeatFeed() {
        feedService.heatFeed();
        verify(userServiceClient).getAllUsersWithKafka();
    }

    @Test
    void testGetFeedWithoutPostIdAndFeedInCache() {
        stubBehavior();
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(redisUser1));
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(postService.getFirstPostsForFeed(redisUser1.getFolloweeIds(), 2)).thenReturn(List.of(postDto1, postDto2));

        List<FeedDto> actualFeed = feedService.getFeed(null);
        List<FeedDto> expectedFeed = getExpectedFeedList();
        assertEquals(expectedFeed, actualFeed);
    }

    @Test
    void testGetFeedWithoutFeedInCache() {
        stubBehavior();
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.ofNullable(redisUser1));
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(postService.getPost(1L)).thenReturn(postDto1);
        when(postService.getNextPostsForFeed(redisUser1.getFolloweeIds(), TEST_TIME1, 2))
                .thenReturn(List.of(postDto1, postDto2));

        List<FeedDto> actualFeed = feedService.getFeed(1L);
        List<FeedDto> expectedFeed = getExpectedFeedList();
        assertEquals(expectedFeed, actualFeed);
    }

    @Test
    void testGetFeedWithoutPostId() {
        stubBehavior();
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.of(getRedisFeed()));
        when(redisPostRepository.findById(1L)).thenReturn(Optional.ofNullable(redisPost1));
        when(redisPostRepository.findById(2L)).thenReturn(Optional.ofNullable(redisPost2));

        List<FeedDto> actualFeed = feedService.getFeed(null);
        List<FeedDto> expectedFeed = getExpectedFeedList();
        assertEquals(expectedFeed, actualFeed);
    }

    @Test
    void testGetFeed() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.of(getRedisFeed()));
        when(redisUserRepository.findById(3L)).thenReturn(Optional.ofNullable(redisUser3));
        when(redisPostRepository.findById(1L)).thenReturn(Optional.ofNullable(redisPost1));
        when(redisPostRepository.findById(2L)).thenReturn(Optional.ofNullable(redisPost2));

        List<FeedDto> actualFeed = feedService.getFeed(1L);
        List<FeedDto> expectedFeed = getExpectedFeedList();
        expectedFeed.remove(0);
        assertEquals(expectedFeed, actualFeed);
    }

    @Test
    void testHeatUserFeed() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("user1")
                .followeeIds(List.of(2L, 3L, 4L))
                .followerIds(List.of(5L, 6L))
                .build();
        when(postService.getFirstPostsForFeed(userDto.getFolloweeIds(), 2)).thenReturn(List.of(postDto1, postDto2));
        when(redisPostRepository.existsById(1L)).thenReturn(true);
        when(redisPostRepository.existsById(2L)).thenReturn(true);
        when(redisUserRepository.findById(2L)).thenReturn(Optional.ofNullable(redisUser2));
        when(redisUserRepository.findById(3L)).thenReturn(Optional.ofNullable(redisUser3));
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.empty());

        feedService.heatUserFeed(userDto);
        verify(redisFeedRepository).save(getRedisFeed());
    }

    private void stubBehavior() {
        when(userContext.getUserId()).thenReturn(USER_ID);
        when(redisUserRepository.findById(2L)).thenReturn(Optional.ofNullable(redisUser2));
        when(redisUserRepository.findById(3L)).thenReturn(Optional.ofNullable(redisUser3));
    }

    private List<FeedDto> getExpectedFeedList() {
        FeedDto feed1 = FeedDto.builder()
                .userId(2L)
                .username("user2")
                .postId(1L)
                .content("post1")
                .publishedAt(TEST_TIME1)
                .build();
        FeedDto feed2 = FeedDto.builder()
                .userId(3L)
                .username("user3")
                .postId(2L)
                .content("post2")
                .publishedAt(TEST_TIME2)
                .build();

        List<FeedDto> list = new ArrayList<>();
        list.add(feed1);
        list.add(feed2);
        return list;
    }

    private RedisFeed getRedisFeed() {
        SortedSet<TimePostId> set = new TreeSet<>();
        set.add(TimePostId.builder()
                .postId(1L)
                .publishedAt(TEST_TIME1)
                .build());
        set.add(TimePostId.builder()
                .postId(2L)
                .publishedAt(TEST_TIME2)
                .build());

        return RedisFeed.builder()
                .userId(USER_ID)
                .postsId(set)
                .build();
    }
}
