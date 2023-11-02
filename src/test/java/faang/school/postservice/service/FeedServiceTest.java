package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.TimedPostId;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @InjectMocks
    FeedService feedService;
    @Mock
    RedisPostRepository redisPostRepository;
    @Mock
    RedisFeedRepository redisFeedRepository;
    @Mock
    RedisUserRepository redisUserRepository;
    @Mock
    UserContext userContext;
    @Mock
    UserServiceClient userServiceClient;
    @Mock
    PostService postService;
    @Mock
    RedisUserMapper redisUserMapper;
    @Mock
    RedisPostMapper redisPostMapper;
    static final long USER_ID = 1L;
    static final long POST_ID = 1L;

    RedisUser redisUser;
    UserDto userDto;
    PostDto postDto;
    RedisPost redisPost;
    TimedPostId timedPostId;
    CommentDto commentDto;
    RedisCommentDto redisCommentDto;
    KafkaPostDto kafkaPostDto;
    RedisFeed redisFeed;
    LocalDateTime localDateTime;
    LikeDto likeDto;


    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.of(2023, 10, 10, 10, 10);
        SortedSet<TimedPostId> feed = new TreeSet<>();
        redisUser = RedisUser.builder()
                .id(USER_ID)
                .followeeIds(List.of(2L, 3L))
                .followerIds(List.of(2L, 3L))
                .smallFileId("123")
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .followeeIds(List.of(2L, 3L))
                .followerIds(List.of(2L, 3L))
                .smallFileId("123")
                .build();
        postDto = PostDto.builder()
                .id(POST_ID)
                .authorId(USER_ID)
                .content("Post 1 content")
                .publishedAt(localDateTime)
                .build();
        redisPost = RedisPost.builder()
                .id(POST_ID)
                .authorId(USER_ID)
                .content("Post 1 content")
                .publishedAt(localDateTime)
                .likes(1)
                .build();
        timedPostId = TimedPostId.builder()
                .postId(redisPost.getId())
                .publishedAt(redisPost.getPublishedAt())
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .content("Comment 1 content")
                .build();
        redisCommentDto = RedisCommentDto.builder()
                .id(1L)
                .content("Comment 1 content")
                .likes(1)
                .build();
        kafkaPostDto = KafkaPostDto.builder()
                .post(timedPostId)
                .userId(USER_ID)
                .build();
        redisFeed = RedisFeed.builder()
                .userId(USER_ID)
                .postIds(feed)
                .build();
        likeDto = LikeDto.builder()
                .commentId(1L)
                .postId(1L)
                .build();
        feed.add(timedPostId);
    }

    @Test
    void testFirstPageFeedFromDbDelivered() {
        when(userContext.getUserId()).thenReturn(1L);
        when(redisFeedRepository.findById(1L)).thenReturn(Optional.empty());
        when(redisUserRepository.findById(1L)).thenReturn(Optional.of(redisUser));
        ReflectionTestUtils.setField(feedService, "postsBatchSize", 1);
        when(postService.getFirstPostsForFeed(List.of(2L, 3L), 1)).thenReturn(new ArrayList<>(List.of(postDto)));
        when(redisPostMapper.toRedisPost(postDto)).thenReturn(redisPost);
        List<FeedDto> feed = feedService.getFeed(null);
        FeedDto expected = FeedDto.builder()
                .postId(1L)
                .content("Post 1 content")
                .authorId(1L)
                .smallFileId("123")
                .likes(1)
                .publishedAt(localDateTime)
                .build();
        assertEquals(List.of(expected),feed);
    }

    @Test
    void testHeatFeed() {
        feedService.heatFeed();
        verify(userServiceClient).getAllUsersWithKafka();
    }
}