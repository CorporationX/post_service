package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.PostPair;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.LikeAction;
import faang.school.postservice.dto.redis.RedisCommentDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.dto.redis.RedisUserDto;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapperImpl;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisPostMapperImpl;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.mapper.redis.RedisUserMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FeedServiceTest {

    @Mock
    private RedisFeedRepository redisFeedRepository;
    @Mock
    private PostService postService;
    @Mock
    private RedisCacheService redisCacheService;
    @Spy
    private RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();
    @Spy
    private RedisPostMapper redisPostMapper = new RedisPostMapperImpl(redisCommentMapper);
    @Spy
    private RedisUserMapper redisUserMapper = new RedisUserMapperImpl();
    @Mock
    private UserContext userContext;
    @Mock
    private RedisKeyValueTemplate redisKeyValueTemplate;
    @InjectMocks
    private FeedService feedService;

    private RedisFeed redisFeed;

    private LinkedHashSet<PostPair> postPairs;

    private List<Long> followerIds;
    private List<Long> followeeIds;

    private Post post;

    private UserDto userDto;

    private RedisUser redisUser;

    private RedisPost redisPost;

    private RedisCommentDto redisCommentDto;
    private RedisCommentDto updateRedisCommentDto;

    private PostPair postPair;
    private PostPair existedPostPair;

    private LocalDateTime publishedAt;

    private final Long userId = 1L;
    private final Long firstUserFolloweeId = 2L;
    private final Long secondUserFolloweeId = 3L;
    private final Long firstUserFollowerId = 2L;
    private final Long secondUserFollowerId = 3L;
    private final Long postId = 1L;
    private final Long existedPostId = 2L;
    private final Long commentId = 1L;

    private LocalDateTime existedPublishedAt;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(feedService, "responseFeedSize", 20);
        ReflectionTestUtils.setField(feedService, "maxFeedInRedis", 500);
        ReflectionTestUtils.setField(feedService, "maxAmountOfComments", 3);
        publishedAt = LocalDateTime.now().minusMonths(1);
        existedPublishedAt = LocalDateTime.now().minusMonths(2);
        postPair = PostPair.builder()
                .postId(postId)
                .publishedAt(publishedAt)
                .build();
        existedPostPair = PostPair.builder()
                .postId(existedPostId)
                .publishedAt(existedPublishedAt)
                .build();
        postPairs = new LinkedHashSet<>(List.of(existedPostPair));
        redisFeed = RedisFeed.builder()
                .userId(userId)
                .posts(postPairs)
                .build();
        post = Post.builder()
                .id(postId)
                .content("Post Content")
                .build();
        redisPost = RedisPost.builder()
                .postId(postId)
                .content("Redis Post Content")
                .postLikes(0L)
                .version(1)
                .authorId(userId)
                .publishedAt(publishedAt)
                .build();
        redisCommentDto = RedisCommentDto.builder()
                .id(commentId)
                .authorId(userId)
                .content("Comment Content")
                .amountOfLikes(5)
                .build();
        updateRedisCommentDto = RedisCommentDto.builder()
                .id(commentId)
                .authorId(userId)
                .content("Updated Comment Content")
                .amountOfLikes(7)
                .build();
        followerIds = new ArrayList<>(List.of(firstUserFollowerId, secondUserFollowerId));
        followeeIds = new ArrayList<>(List.of(firstUserFolloweeId, secondUserFolloweeId));
        userDto = UserDto.builder()
                .id(userId)
                .username("User")
                .email("user@gmail.com")
                .aboutMe("Some Information")
                .followerIds(followerIds)
                .followeeIds(followeeIds)
                .build();
        redisUser = RedisUser.builder()
                .userId(userId)
                .username("User")
                .email("user@gmail.com")
                .followerIds(followerIds)
                .followeeIds(followeeIds)
                .version(1)
                .build();
    }

    @Test
    void getUserFeedPostIdIsMissingFirstScenarioTest() {
        when(userContext.getUserId()).thenReturn(userId);
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.of(redisFeed));
        when(postService.findRedisPostsByAndCacheThemIfNotExist(List.of(existedPostId)))
                .thenReturn(new ArrayList<>(List.of(redisPost)));
        when(redisCacheService.findUserBy(userId)).thenReturn(userDto);
        when(postService.findSortedPostsByAuthorIdsNotInPostIdsLimit(followeeIds, List.of(existedPostId), 499))
                .thenReturn(new ArrayList<>(List.of(post)));
        when(redisCacheService.mapPostToRedisPostAndSetDefaultVersion(post)).thenReturn(redisPost);
        when(redisCacheService.findOrCacheRedisUser(userId)).thenReturn(redisUser);

        RedisUserDto redisUserDto = RedisUserDto.builder()
                .userId(userId)
                .username("User")
                .email("user@gmail.com")
                .followeeIds(followeeIds)
                .followerIds(followerIds)
                .build();
        RedisPostDto firstExpected = RedisPostDto.builder()
                .postId(postId)
                .content("Redis Post Content")
                .userDto(redisUserDto)
                .postLikes(0L)
                .publishedAt(publishedAt)
                .build();
        RedisPostDto secondExpected = RedisPostDto.builder()
                .postId(postId)
                .content("Redis Post Content")
                .userDto(redisUserDto)
                .postLikes(0L)
                .publishedAt(publishedAt)
                .build();
        FeedDto expected = FeedDto.builder()
                .requesterId(userId)
                .dtos(List.of(firstExpected, secondExpected))
                .build();

        FeedDto result = feedService.getUserFeedBy(null);

        assertEquals(expected, result);
        assertEquals(2, result.getDtos().size());

        verify(userContext).getUserId();
        verify(redisFeedRepository).findById(userId);
        verify(postService).findRedisPostsByAndCacheThemIfNotExist(List.of(existedPostId));
        verify(redisCacheService).findUserBy(userId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(postService).findSortedPostsByAuthorIdsNotInPostIdsLimit(followeeIds, List.of(existedPostId), 499);
        verify(redisCacheService).updateOrCachePost(post);
        verify(redisCacheService).mapPostToRedisPostAndSetDefaultVersion(post);
        verify(postService).publishPostViewEventToKafka(List.of(postId, postId));
        verify(redisCacheService, times(2)).findOrCacheRedisUser(userId);
    }

    @Test
    void getUserFeedPostIdIsMissingSecondScenarioTest() {
        when(userContext.getUserId()).thenReturn(userId);
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.empty());
        when(redisCacheService.findUserBy(userId)).thenReturn(userDto);
        when(postService.findSortedPostsByAuthorIdsLimit(followeeIds, 500))
                .thenReturn(new ArrayList<>(List.of(post)));
        when(redisCacheService.updateOrCachePost(post)).thenReturn(redisPost);
        when(redisCacheService.findOrCacheRedisUser(userId)).thenReturn(redisUser);

        RedisUserDto redisUserDto = RedisUserDto.builder()
                .userId(userId)
                .username("User")
                .email("user@gmail.com")
                .followeeIds(followeeIds)
                .followerIds(followerIds)
                .build();
        RedisPostDto postDtoExpected = RedisPostDto.builder()
                .postId(postId)
                .content("Redis Post Content")
                .userDto(redisUserDto)
                .postLikes(0L)
                .publishedAt(publishedAt)
                .build();
        FeedDto expected = FeedDto.builder()
                .requesterId(userId)
                .dtos(List.of(postDtoExpected))
                .build();

        FeedDto result = feedService.getUserFeedBy(null);

        assertEquals(expected, result);
        assertEquals(1, result.getDtos().size());

        verify(redisFeedRepository).findById(userId);
        verify(redisCacheService).findUserBy(userId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(postService).findSortedPostsByAuthorIdsLimit(followeeIds, 500);
        verify(redisCacheService).updateOrCachePost(post);
        verify(redisCacheService).findOrCacheRedisUser(userId);
        verify(redisFeedRepository).save(any(RedisFeed.class));
    }

    @Test
    void getUserFeedPostIdIsMissingThirdScenarioTest() {
        UserDto emptyUser = new UserDto();
        when(userContext.getUserId()).thenReturn(userId);
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.empty());
        when(redisCacheService.findUserBy(userId)).thenReturn(emptyUser);

        FeedDto expected = FeedDto.builder()
                .requesterId(userId)
                .dtos(Collections.emptyList())
                .build();
        FeedDto result = feedService.getUserFeedBy(null);

        assertEquals(expected, result);
        assertTrue(result.getDtos().isEmpty());

        verify(redisFeedRepository).findById(userId);
        verify(redisCacheService).findUserBy(userId);
        verify(redisCacheService).updateOrCacheUser(emptyUser);
        verify(redisFeedRepository).save(RedisFeed.builder()
                .userId(userId)
                .posts(new LinkedHashSet<>())
                .build());
    }

    @Test
    void getFeedAfterPostWithIdFirstScenarioTest() {
        when(userContext.getUserId()).thenReturn(userId);
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.of(redisFeed));
        when(redisCacheService.findUserBy(userId)).thenReturn(userDto);
        when(postService.findRedisPostAndCacheHimIfNotExist(existedPostId)).thenReturn(redisPost);
        when(postService.findSortedPostsFromPostDateAndAuthorsLimit(followeeIds, publishedAt, 499))
                .thenReturn(new ArrayList<>(List.of(post)));
        when(redisCacheService.updateOrCachePost(post)).thenReturn(redisPost);
        when(redisCacheService.findOrCacheRedisUser(userId)).thenReturn(redisUser);

        RedisUserDto redisUserDto = RedisUserDto.builder()
                .userId(userId)
                .username("User")
                .email("user@gmail.com")
                .followeeIds(followeeIds)
                .followerIds(followerIds)
                .build();
        RedisPostDto postDtoExpected = RedisPostDto.builder()
                .postId(postId)
                .content("Redis Post Content")
                .userDto(redisUserDto)
                .postLikes(0L)
                .publishedAt(publishedAt)
                .build();
        FeedDto expected = FeedDto.builder()
                .requesterId(userId)
                .dtos(List.of(postDtoExpected))
                .build();

        FeedDto result = feedService.getUserFeedBy(existedPostId);

        assertEquals(expected, result);
        assertEquals(1, result.getDtos().size());

        verify(redisFeedRepository).findById(userId);
        verify(redisCacheService).findUserBy(userId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(postService).findRedisPostAndCacheHimIfNotExist(existedPostId);
        verify(postService).findSortedPostsFromPostDateAndAuthorsLimit(followeeIds, publishedAt, 499);
        verify(postService).publishPostViewEventToKafka(List.of(postId));
        verify(redisCacheService).updateOrCachePost(post);
        verify(redisFeedRepository).save(any(RedisFeed.class));
        verify(redisCacheService).findOrCacheRedisUser(userId);
    }

    @Test
    void getFeedAfterPostWithIdSecondScenarioTest() {
        redisFeed.setPosts(new LinkedHashSet<>(List.of(postPair, existedPostPair)));
        when(userContext.getUserId()).thenReturn(userId);
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.of(redisFeed));
        when(postService.findRedisPostAndCacheHimIfNotExist(existedPostId)).thenReturn(redisPost);
        when(redisCacheService.findOrCacheRedisUser(userId)).thenReturn(redisUser);
        when(postService.findSortedPostsByAuthorIdsNotInPostIdsLimit(followeeIds, List.of(postId, existedPostId), 18))
                .thenReturn(new ArrayList<>(List.of(post)));
        when(redisCacheService.updateOrCachePost(post)).thenReturn(redisPost);

        RedisUserDto redisUserDto = RedisUserDto.builder()
                .userId(userId)
                .username("User")
                .email("user@gmail.com")
                .followeeIds(followeeIds)
                .followerIds(followerIds)
                .build();
        RedisPostDto postDtoExpected = RedisPostDto.builder()
                .postId(postId)
                .content("Redis Post Content")
                .userDto(redisUserDto)
                .postLikes(0L)
                .publishedAt(publishedAt)
                .build();
        FeedDto expected = FeedDto.builder()
                .requesterId(userId)
                .dtos(List.of(postDtoExpected, postDtoExpected))
                .build();

        FeedDto result = feedService.getUserFeedBy(postId);

        assertEquals(expected, result);

        verify(userContext).getUserId();
        verify(redisFeedRepository).findById(userId);
        verify(postService).findRedisPostAndCacheHimIfNotExist(existedPostId);
        verify(postService).findSortedPostsByAuthorIdsNotInPostIdsLimit(followeeIds, List.of(postId, existedPostId), 18);
        verify(postService).publishPostViewEventToKafka(List.of(postId));
        verify(redisCacheService).updateOrCachePost(post);
        verify(redisCacheService, times(3)).findOrCacheRedisUser(userId);
    }

    @Test
    void getFeedAfterPostWithIdThirdScenarioTest() {
        when(userContext.getUserId()).thenReturn(userId);
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.empty());
        when(redisCacheService.findUserBy(userId)).thenReturn(userDto);
        when(postService.findRedisPostAndCacheHimIfNotExist(postId)).thenReturn(redisPost);
        when(postService.findSortedPostsFromPostDateAndAuthorsLimit(followeeIds, publishedAt, 500))
                .thenReturn(new ArrayList<>(List.of(post)));
        when(redisCacheService.updateOrCachePost(post)).thenReturn(redisPost);
        when(redisCacheService.findOrCacheRedisUser(userId)).thenReturn(redisUser);

        RedisUserDto redisUserDto = RedisUserDto.builder()
                .userId(userId)
                .username("User")
                .email("user@gmail.com")
                .followeeIds(followeeIds)
                .followerIds(followerIds)
                .build();
        RedisPostDto postDtoExpected = RedisPostDto.builder()
                .postId(postId)
                .content("Redis Post Content")
                .userDto(redisUserDto)
                .postLikes(0L)
                .publishedAt(publishedAt)
                .build();
        FeedDto expected = FeedDto.builder()
                .requesterId(userId)
                .dtos(List.of(postDtoExpected))
                .build();

        FeedDto result = feedService.getUserFeedBy(postId);

        assertEquals(expected, result);

        verify(userContext).getUserId();
        verify(redisFeedRepository).findById(userId);
        verify(redisCacheService).findUserBy(userId);
        verify(redisCacheService).updateOrCacheUser(userDto);
        verify(postService).findRedisPostAndCacheHimIfNotExist(postId);
        verify(postService).findSortedPostsFromPostDateAndAuthorsLimit(followeeIds, publishedAt, 500);
        verify(postService).publishPostViewEventToKafka(List.of(postId));
        verify(redisCacheService).updateOrCachePost(post);
        verify(redisFeedRepository).save(any(RedisFeed.class));
        verify(redisCacheService).findOrCacheRedisUser(userId);
    }

    @Test
    void saveSinglePostToFeedExistScenario() {
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.of(redisFeed));

        RedisFeed expected = RedisFeed.builder()
                .userId(userId)
                .posts(new LinkedHashSet<>(List.of(postPair, existedPostPair)))
                .build();

        feedService.saveSinglePostToFeed(userId, postPair);

        verify(redisFeedRepository).findById(userId);
        verify(redisKeyValueTemplate).update(userId, expected);
    }

    @Test
    void saveSinglePostToFeedNotExistScenario() {
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.empty());

        RedisFeed expected = RedisFeed.builder()
                .userId(userId)
                .posts(new LinkedHashSet<>(List.of(postPair)))
                .build();

        feedService.saveSinglePostToFeed(userId, postPair);

        verify(redisFeedRepository).findById(userId);
        verify(redisFeedRepository).save(expected);
    }

    @Test
    void deleteSinglePostInFeedFirstScenarioTest() {
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.of(redisFeed));

        feedService.deleteSinglePostInFeed(List.of(userId), existedPostId);

        assertEquals(0, postPairs.size());

        verify(redisFeedRepository).findById(userId);
    }

    @Test
    void deleteSinglePostInFeedSecondScenarioTest() {
        when(redisFeedRepository.findById(userId)).thenReturn(Optional.empty());

        feedService.deleteSinglePostInFeed(List.of(userId), postId);

        verify(redisCacheService).deleteRedisPost(postId);
    }

    @Test
    void updateSinglePostInRedisFirstScenarioTest() {
        when(postService.findAlreadyPublishedAndNotDeletedPost(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.updateSinglePostInRedis(postId);

        verify(postService).findAlreadyPublishedAndNotDeletedPost(postId);
        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).updatePost(redisPost, post);
    }

    @Test
    void updateSinglePostInRedisSecondScenarioTest() {
        when(postService.findAlreadyPublishedAndNotDeletedPost(postId)).thenReturn(Optional.of(post));
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.empty());

        feedService.updateSinglePostInRedis(postId);

        verify(postService).findAlreadyPublishedAndNotDeletedPost(postId);
        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).cachePost(post);
    }

    @Test
    void updateSinglePostInRedisThirdScenarioTest() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> feedService.updateSinglePostInRedis(postId));

        assertEquals("Post with ID: 1 doesn't exist", exception.getMessage());
    }

    @Test
    void addCommentToPostFirstScenarioTestTest() {
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.addCommentToPost(postId, redisCommentDto);

        assertEquals(2, redisPost.getVersion());
        assertEquals(1, redisPost.getCommentsDto().size());
        assertEquals(redisCommentDto, redisPost.getCommentsDto().get(0));

        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).updateRedisPost(postId, redisPost);
    }

    @Test
    void addCommentToPostSecondScenarioTestTest() {
        RedisCommentDto firstEmptyComment = new RedisCommentDto();
        RedisCommentDto secondEmptyComment = new RedisCommentDto();
        RedisCommentDto thirdEmptyComment = new RedisCommentDto();

        List<RedisCommentDto> redisCommentDtos = new ArrayList<>(List.of(firstEmptyComment, secondEmptyComment, thirdEmptyComment));
        redisPost.setCommentsDto(redisCommentDtos);

        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.addCommentToPost(postId, redisCommentDto);

        assertEquals(3, redisPost.getCommentsDto().size());
        assertEquals(redisCommentDto, redisPost.getCommentsDto().get(0));
        assertEquals(2, redisPost.getVersion());
    }

    @Test
    void addCommentToPostThirdScenarioTestTest() {
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.empty());
        when(postService.findAlreadyPublishedAndNotDeletedPost(postId)).thenReturn(Optional.of(post));

        feedService.addCommentToPost(postId, redisCommentDto);

        verify(postService).findAlreadyPublishedAndNotDeletedPost(postId);
        verify(redisCacheService).cachePost(post);
    }

    @Test
    void updateCommentInPostFirstScenarioTest() {
        RedisCommentDto secondComment = RedisCommentDto.builder()
                .id(2L)
                .authorId(userId)
                .content("Second Comment Content")
                .amountOfLikes(3)
                .build();
        RedisCommentDto thirdComment = RedisCommentDto.builder()
                .id(3L)
                .authorId(userId)
                .content("Third Comment Content")
                .amountOfLikes(1)
                .build();
        List<RedisCommentDto> commentDtos = new ArrayList<>(List.of(redisCommentDto, secondComment, thirdComment));
        redisPost.setCommentsDto(commentDtos);

        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.updateCommentInPost(postId, updateRedisCommentDto);

        assertEquals(updateRedisCommentDto, redisPost.getCommentsDto().get(0));
        assertEquals(2, redisPost.getVersion());

        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).updateRedisPost(postId, redisPost);
    }

    @Test
    void updateCommentInPostSecondScenarioTest() {
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.empty());
        when(postService.findAlreadyPublishedAndNotDeletedPost(postId)).thenReturn(Optional.of(post));

        feedService.updateCommentInPost(postId, updateRedisCommentDto);

        verify(postService).findAlreadyPublishedAndNotDeletedPost(postId);
        verify(redisCacheService).cachePost(post);
    }

    @Test
    void deleteCommentFromPostFirstScenarioTest() {
        redisPost.setCommentsDto(new ArrayList<>(List.of(redisCommentDto)));

        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.deleteCommentFromPost(postId, commentId);

        assertEquals(0, redisPost.getCommentsDto().size());
        assertEquals(2, redisPost.getVersion());

        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).updateRedisPost(postId, redisPost);
    }

    @Test
    void deleteCommentFromPostSecondScenarioTest() {
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.empty());
        when(postService.findAlreadyPublishedAndNotDeletedPost(postId)).thenReturn(Optional.of(post));

        feedService.deleteCommentFromPost(postId, commentId);

        verify(redisCacheService).findRedisPostBy(postId);
        verify(postService).findAlreadyPublishedAndNotDeletedPost(postId);
        verify(redisCacheService).cachePost(post);
    }

    @Test
    void incrementOrDecrementPostLikeIncrementScenarioTest() {
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.incrementOrDecrementPostLike(postId, LikeAction.ADD);

        assertEquals(1, redisPost.getPostLikes());
        assertEquals(2, redisPost.getVersion());

        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).updateRedisPost(postId, redisPost);
    }

    @Test
    void incrementOrDecrementPostLikeDecrementScenarioTest() {
        redisPost.setPostLikes(5L);
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.incrementOrDecrementPostLike(postId, LikeAction.REMOVE);

        assertEquals(4, redisPost.getPostLikes());
        assertEquals(2, redisPost.getVersion());
    }

    @Test
    void incrementOrDecrementPostCommentLikeIncrementTest() {
        redisPost.setCommentsDto(new ArrayList<>(List.of(redisCommentDto)));
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.incrementOrDecrementPostCommentLike(postId, commentId, LikeAction.ADD);

        assertEquals(6, redisPost.getCommentsDto().get(0).getAmountOfLikes());
        assertEquals(2, redisPost.getVersion());

        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).updateRedisPost(postId, redisPost);
    }

    @Test
    void incrementOrDecrementPostCommentLikeDecrementTest() {
        redisPost.setCommentsDto(new ArrayList<>(List.of(redisCommentDto)));
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.incrementOrDecrementPostCommentLike(postId, commentId, LikeAction.REMOVE);

        assertEquals(4, redisPost.getCommentsDto().get(0).getAmountOfLikes());
        assertEquals(2, redisPost.getVersion());
    }

    @Test
    void incrementPostViewFirstScenarioTest() {
        redisPost.setPostViews(5L);
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.of(redisPost));

        feedService.incrementPostView(postId);

        assertEquals(6, redisPost.getPostViews());
        assertEquals(2, redisPost.getVersion());

        verify(redisCacheService).findRedisPostBy(postId);
        verify(redisCacheService).updateRedisPost(postId, redisPost);
    }

    @Test
    void incrementPostViewSecondScenarioTest() {
        when(redisCacheService.findRedisPostBy(postId)).thenReturn(Optional.empty());
        when(postService.findAlreadyPublishedAndNotDeletedPost(postId)).thenReturn(Optional.of(post));

        feedService.incrementPostView(postId);

        verify(redisCacheService).findRedisPostBy(postId);
        verify(postService).incrementPostViewByPostId(postId);
        verify(postService).findAlreadyPublishedAndNotDeletedPost(postId);
        verify(redisCacheService).cachePost(post);
    }

    @Test
    void mapAndUpdateOrCachePostInRedisTest() {
        when(redisCacheService.updateOrCachePost(post)).thenReturn(redisPost);

        List<RedisPost> result = feedService.mapAndUpdateOrCachePostInRedis(List.of(post));

        assertIterableEquals(List.of(redisPost), result);

        verify(redisCacheService).updateOrCachePost(post);
    }
}
