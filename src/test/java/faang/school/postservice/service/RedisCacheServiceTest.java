package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.dto.kafka.KafkaPostDto;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.TimePostId;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.kafka.producer.KafkaFeedProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisCacheServiceTest {

    @InjectMocks
    private RedisCacheService redisCacheService;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private RedisUserRepository redisUserRepository;
    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private KafkaFeedProducer kafkaFeedProducer;
    @Mock
    private RedisKeyValueTemplate redisTemplate;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private RedisPostMapper redisPostMapper;
    @Mock
    private RedisUserMapper redisUserMapper;
    @Mock
    private  RedisCommentMapper redisCommentMapper;

    private RedisUser redisUser;
    private RedisPost redisPost;
    private UserDto userDto;
    private PostDto postDto;
    private RedisFeed redisFeed;
    private RedisCommentDto redisCommentDto;
    private CommentDto commentDto;
    private KafkaPostDto kafkaPostDto;
    private LikeDto likeDto;
    private TimePostId timePostId;
    private List<RedisCommentDto> listOfRedisCommentDto;
    private final LocalDateTime TEST_TIME = LocalDateTime.of(2023, 10, 10, 10, 10);
    static final long USER_ID = 1L;
    static final long POST_ID = 1L;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(redisCacheService, "maxCommentsInCache", 3);
        ReflectionTestUtils.setField(redisCacheService, "feedBatchSize", 2);
        SortedSet<TimePostId> feed = new TreeSet<>();
        redisUser = RedisUser.builder()
                .id(USER_ID)
                .followeeIds(List.of(2L, 3L))
                .followerIds(List.of(2L, 3L))
                .pictureFileId("123")
                .build();
        userDto = UserDto.builder()
                .id(USER_ID)
                .followeeIds(List.of(2L, 3L))
                .followerIds(List.of(2L, 3L))
                .pictureFileId("123")
                .build();
        postDto = PostDto.builder()
                .id(POST_ID)
                .authorId(USER_ID)
                .content("Post 1 content")
                .publishedAt(TEST_TIME)
                .build();
        redisPost = RedisPost.builder()
                .id(POST_ID)
                .userId(USER_ID)
                .content("Post 1 content")
                .publishedAt(TEST_TIME)
                .likes(1)
                .build();
        timePostId = TimePostId.builder()
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
                .timePostId(timePostId)
                .userId(USER_ID)
                .build();
        redisFeed = RedisFeed.builder()
                .userId(USER_ID)
                .postsId(feed)
                .build();
        likeDto = LikeDto.builder()
                .commentId(1L)
                .postId(1L)
                .build();
        feed.add(timePostId);
        listOfRedisCommentDto = new ArrayList<>(List.of(redisCommentDto));
    }

    @Test
    void testOnlyPostWithoutCommentsInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.of(redisUser));
        when(redisPostMapper.toEntity(postDto)).thenReturn(redisPost);

        redisCacheService.savePublishedPost(postDto);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testPostAndUserInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(redisUserMapper.toEntity(userDto)).thenReturn(redisUser);
        when(redisUserRepository.save(redisUser)).thenReturn(redisUser);
        when(redisPostMapper.toEntity(postDto)).thenReturn(redisPost);

        redisCacheService.savePublishedPost(postDto);
        verify(redisUserRepository).save(redisUser);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testOnlyPostWithOneCommentInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.of(redisUser));
        when(redisPostMapper.toEntity(postDto)).thenReturn(redisPost);
        postDto.setComments(List.of(commentDto));
        redisPost.setRedisComments(List.of(redisCommentDto));

        redisCacheService.savePublishedPost(postDto);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testOnlyPostWithCoupleCommentsInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.of(redisUser));
        when(redisPostMapper.toEntity(postDto)).thenReturn(redisPost);
        postDto.setComments(List.of(commentDto, commentDto, commentDto, commentDto));
        redisPost.setRedisComments(List.of(redisCommentDto, redisCommentDto, redisCommentDto, redisCommentDto));

        redisCacheService.savePublishedPost(postDto);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testPostInExistingNotFullFeedIsAdded() {
        SortedSet<TimePostId> feed = new TreeSet<>();
        feed.add(timePostId);
        redisFeed.setPostsId(feed);
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.of(redisFeed));

        redisCacheService.saveFeed(kafkaPostDto);
        verify(redisTemplate).update(redisFeed);
    }

    @Test
    void testPostInExistingFullFeedIsAdded() {
        SortedSet<TimePostId> feed = new TreeSet<>();
        feed.add(timePostId);
        feed.add(TimePostId.builder()
                .postId(2L)
                .publishedAt(TEST_TIME)
                .build());
        redisFeed.setPostsId(feed);
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.of(redisFeed));

        redisCacheService.saveFeed(kafkaPostDto);
        verify(redisTemplate).update(redisFeed);
    }

    @Test
    void testFeedIsCreatedAndPostInFeedIsAdded() {
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.empty());
        redisCacheService.saveFeed(kafkaPostDto);
        verify(redisFeedRepository).save(redisFeed);
    }

    @Test
    void testPostFromFeedIsDeleted() {
        when(redisFeedRepository.findById(1L)).thenReturn(Optional.of(redisFeed));
        redisCacheService.deletePostFromFeed(kafkaPostDto);
        verify(redisTemplate).update(redisFeed);
    }

    @Test
    void testPostFromCacheIsDeleted() {
        when(redisUserRepository.findById(1L)).thenReturn(Optional.of(redisUser));
        redisCacheService.deletePostFromCache(postDto);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.DELETE, redisUser.getFollowerIds(),timePostId);
    }

    @Test
    void testPostInCacheIsUpdated() {
        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(redisPost));
        redisCacheService.updatePostInCache(postDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void addCommentToPost() {
        redisPost.setRedisComments(listOfRedisCommentDto);
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        when(redisCommentMapper.toRedisDto(CommentDto.builder().build())).thenReturn(RedisCommentDto.builder().build());

        redisCacheService.addCommentToPost(CommentDto.builder().build());
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testUpdateCommentOnPost() {
        redisPost.setRedisComments(listOfRedisCommentDto);
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        redisCacheService.updateCommentInCache(commentDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testDeleteCommentFromPost() {
        redisPost.setRedisComments(listOfRedisCommentDto);
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        when(commentRepository.findThreeLastComments(1L)).thenReturn(new ArrayList<>(List.of(Comment.builder().build())));
        when(redisCommentMapper.toRedisDto(Comment.builder().build())).thenReturn(redisCommentDto);

        redisCacheService.deleteCommentFromPost(commentDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testAddLikeOnPost() {
        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(redisPost));
        redisCacheService.addLikeToPost(likeDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testDeleteLikeFromPost() {
        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(redisPost));
        redisCacheService.deleteLikeFromPost(likeDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testAddLikeToComment() {
        redisPost.setRedisComments(listOfRedisCommentDto);
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        redisCacheService.addLikeToComment(likeDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testDeleteLikeFromComment() {
        redisPost.setRedisComments(listOfRedisCommentDto);
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        redisCacheService.deleteLikeFromComment(likeDto);
        verify(redisTemplate).update(redisPost);
    }
}
