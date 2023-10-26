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
class RedisCacheServiceTest {

    @InjectMocks
    RedisCacheService redisCacheService;
    @Mock
    RedisPostRepository redisPostRepository;
    @Mock
    RedisUserRepository redisUserRepository;
    @Mock
    RedisFeedRepository redisFeedRepository;
    @Mock
    UserServiceClient userServiceClient;
    @Mock
    KafkaFeedProducer kafkaFeedProducer;
    @Mock
    RedisKeyValueTemplate redisTemplate;
    @Mock
    CommentRepository commentRepository;
    @Mock
    RedisPostMapper redisPostMapper;
    @Mock
    RedisUserMapper redisUserMapper;
    @Mock
    RedisCommentMapper redisCommentMapper;
    static final long USER_ID = 1L;
    static final long POST_ID = 1L;
    RedisUser redisUser;
    UserDto userDto;
    PostDto postDto;
    RedisPost redisPost;
    TimePostId timePostId;
    CommentDto commentDto;
    RedisCommentDto redisCommentDto;
    KafkaPostDto kafkaPostDto;
    RedisFeed redisFeed;
    LocalDateTime localDateTime;
    LikeDto likeDto;


    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.of(2023, 10, 10, 10, 10);
        SortedSet<TimePostId> feed = new TreeSet<>();
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
                .post(timePostId)
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
        feed.add(timePostId);
    }

    @Test
    void testOnlyPostWithoutCommentsInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.of(redisUser));
        when(redisPostMapper.toRedisPost(postDto)).thenReturn(redisPost);
        redisCacheService.putPostAndAuthorInCache(postDto);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testPostAndUserInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.empty());
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(redisUserMapper.toEntity(userDto)).thenReturn(redisUser);
        when(redisUserRepository.save(redisUser)).thenReturn(redisUser);
        when(redisPostMapper.toRedisPost(postDto)).thenReturn(redisPost);
        redisCacheService.putPostAndAuthorInCache(postDto);
        verify(redisUserRepository).save(redisUser);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testOnlyPostWithOneCommentInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.of(redisUser));
        postDto.setComments(List.of(commentDto));
        redisPost.setRedisCommentDtos(List.of(redisCommentDto));
        when(redisPostMapper.toRedisPost(postDto)).thenReturn(redisPost);
        ReflectionTestUtils.setField(redisCacheService, "maxCommentsInCache", 3);
        redisCacheService.putPostAndAuthorInCache(postDto);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testOnlyPostWithCoupleCommentsInCacheIsAdded() {
        when(redisUserRepository.findById(USER_ID)).thenReturn(Optional.of(redisUser));
        postDto.setComments(List.of(commentDto, commentDto, commentDto, commentDto));
        redisPost.setRedisCommentDtos(List.of(redisCommentDto, redisCommentDto, redisCommentDto, redisCommentDto));
        when(redisPostMapper.toRedisPost(postDto)).thenReturn(redisPost);
        ReflectionTestUtils.setField(redisCacheService, "maxCommentsInCache", 3);
        redisCacheService.putPostAndAuthorInCache(postDto);
        verify(redisPostRepository).save(redisPost);
        verify(kafkaFeedProducer).sendFeed(KafkaKey.CREATE, redisUser.getFollowerIds(), timePostId);
    }

    @Test
    void testPostInExistingNotFullFeedIsAdded() {
        SortedSet<TimePostId> feed = new TreeSet<>();
        feed.add(timePostId);
        redisFeed.setPostIds(feed);
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.of(redisFeed));
        ReflectionTestUtils.setField(redisCacheService, "postsFeedSize", 2);
        redisCacheService.addPostInFeed(kafkaPostDto);
        verify(redisTemplate).update(redisFeed);
    }

    @Test
    void testPostInExistingFullFeedIsAdded() {
        SortedSet<TimePostId> feed = new TreeSet<>();
        feed.add(timePostId);
        feed.add(TimePostId.builder().postId(2L).publishedAt(localDateTime).build());
        redisFeed.setPostIds(feed);
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.of(redisFeed));
        ReflectionTestUtils.setField(redisCacheService, "postsFeedSize", 2);
        redisCacheService.addPostInFeed(kafkaPostDto);
        verify(redisTemplate).update(redisFeed);
    }

    @Test
    void testFeedIsCreatedAndPostInFeedIsAdded() {
        when(redisFeedRepository.findById(USER_ID)).thenReturn(Optional.empty());
        ReflectionTestUtils.setField(redisCacheService, "postsFeedSize", 2);
        redisCacheService.addPostInFeed(kafkaPostDto);
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
        redisPost.setRedisCommentDtos(new ArrayList<>(List.of(redisCommentDto)));
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        when(redisCommentMapper.toRedisDto(CommentDto.builder().build())).thenReturn(RedisCommentDto.builder().build());
        ReflectionTestUtils.setField(redisCacheService, "maxCommentsInCache", 3);
        redisCacheService.addCommentToPost(CommentDto.builder().build());
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testUpdateCommentOnPost() {
        redisPost.setRedisCommentDtos(new ArrayList<>(List.of(redisCommentDto)));
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        redisCacheService.updateCommentOnPost(commentDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testDeleteCommentFromPost() {
        redisPost.setRedisCommentDtos(new ArrayList<>(List.of(redisCommentDto)));
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        ReflectionTestUtils.setField(redisCacheService, "maxCommentsInCache", 3);
        when(commentRepository.findLastThreeComments(1L)).thenReturn(new ArrayList<>(List.of(Comment.builder().build())));
        when(redisCommentMapper.toRedisDto(Comment.builder().build())).thenReturn(redisCommentDto);
        redisCacheService.deleteCommentFromPost(commentDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testAddLikeOnPost() {
        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(redisPost));
        redisCacheService.addLikeOnPost(likeDto);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testDeleteLikeFromPost() {
        when(redisPostRepository.findById(1L)).thenReturn(Optional.of(redisPost));
        redisCacheService.deleteLikeFromPost(1L);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testAddLikeToComment() {
        redisPost.setRedisCommentDtos(new ArrayList<>(List.of(redisCommentDto)));
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        redisCacheService.addLikeToComment(1L,1L);
        verify(redisTemplate).update(redisPost);
    }

    @Test
    void testDeleteLikeFromComment() {
        redisPost.setRedisCommentDtos(new ArrayList<>(List.of(redisCommentDto)));
        when(redisPostRepository.findById(anyLong())).thenReturn(Optional.of(redisPost));
        redisCacheService.deleteLikeFromComment(1L,1L);
        verify(redisTemplate).update(redisPost);
    }
}