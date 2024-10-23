package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.ThreadPoolConfig;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.heater.AuthorProducer;
import faang.school.postservice.heater.CommentProducer;
import faang.school.postservice.heater.FeedProducer;
import faang.school.postservice.heater.Heater;
import faang.school.postservice.heater.LikeProducer;
import faang.school.postservice.heater.PostProducer;
import faang.school.postservice.service.post.CommentService;
import faang.school.postservice.service.post.FeedService;
import faang.school.postservice.service.post.LikeService;
import faang.school.postservice.service.post.PostServiceImpl;
import faang.school.postservice.service.post.RedisCache;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class FeedServiceTest {

    @Mock
    private ZSetOperations<String, String> zSetOperations;
    @Mock
    private UserContext userContext;
    @Mock
    private RedisCache redisCache;
    @Mock
    private PostServiceImpl postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ThreadPoolConfig threadPoolConfig;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeService likeService;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private FeedService feedService;

    private String feed = "feed:";
    private String post = "post:";
    private String comment = "comment:";
    private String infoAboutAuthor = "infoAboutAuthor:";
    private int feedSize = 10;

    private List<Heater> heaterList;
    @Mock
    private PostProducer postProducer;
    @Mock
    private CommentProducer commentProducer;
    @Mock
    private LikeProducer likeProducer;
    @Mock
    private FeedProducer feedProducer;
    @Mock
    private AuthorProducer authorProducer;

    @BeforeEach
    public void setUp() {
        heaterList = new ArrayList<>();
        heaterList.add(postProducer);
        heaterList.add(commentProducer);
        heaterList.add(likeProducer);
        heaterList.add(feedProducer);
        heaterList.add(authorProducer);

        feedService = new FeedService(zSetOperations, userContext, redisCache, userServiceClient, postService,
                heaterList, objectMapper, commentService, likeService, threadPoolConfig);

        feedService.setFeed(feed);
        feedService.setPost(post);
        feedService.setComment(comment);
        feedService.setInfoAboutAuthor(infoAboutAuthor);
        feedService.setFeedSize(feedSize);
    }

    @Test
    public void testGetFeedWithNullArg() {
        long userId = 1L;
        String key = feed + userId;
        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(zSetOperations.reverseRangeByScore(key, Double.MIN_VALUE, Double.MAX_VALUE, 0, feedSize))
                .thenReturn(Collections.emptySet());
        Mockito.when(postService.findPostIdsByFolloweeId(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Collections.emptyList());
        List<FeedDto> actualResult = feedService.getFeed(null);
        Mockito.verify(userContext, Mockito.times(1)).getUserId();
        Mockito.verify(zSetOperations, Mockito.times(1)).reverseRangeByScore(key, Double.MIN_VALUE, Double.MAX_VALUE, 0, feedSize);
        Assertions.assertNull(actualResult);
    }

    @Test
    public void testGetFeed() {
        long userId = 1L;
        Long authorId = 2L;
        Long likeInfo = 10L;
        Double score = 50.0;
        String afterPostId = "post1";
        String key = feed + userId;
        String postId = "post1";
        String postInfo = "{\"authorId\": 2}";
        String authorInfo = "authorInfo";
        Set<String> commentInfo = Set.of("comment1", "comment2");
        Mockito.when(userContext.getUserId()).thenReturn(userId);
        Mockito.when(zSetOperations.score(key, afterPostId)).thenReturn(score);
        Mockito.when(zSetOperations.reverseRangeByScore(key, Double.MIN_VALUE, score - 1, 0, feedSize)).thenReturn(Set.of(postId));
        Mockito.when(redisCache.getFromHSetCache(post, postId)).thenReturn(postInfo);
        Mockito.when(redisCache.getFromHSetCache(infoAboutAuthor, authorId.toString())).thenReturn(authorInfo);
        Mockito.when(redisCache.getAllZSetValues(comment + postId)).thenReturn(commentInfo);
        Mockito.when(redisCache.getZSetSize(comment + postId)).thenReturn(likeInfo);
        List<FeedDto> result = feedService.getFeed(afterPostId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.size());
        FeedDto feedDto = result.get(0);
        Assertions.assertEquals(postInfo, feedDto.getPostInfo());
        Assertions.assertEquals(authorInfo, feedDto.getAuthorInfo());
        Assertions.assertEquals(commentInfo, feedDto.getCommentInfo());
        Assertions.assertEquals(likeInfo, feedDto.getLikeInfo());
        Mockito.verify(userContext, Mockito.times(1)).getUserId();
        Mockito.verify(zSetOperations, Mockito.times(1)).score(key, afterPostId);
        Mockito.verify(zSetOperations, Mockito.times(1)).reverseRangeByScore(key, Double.MIN_VALUE, score - 1, 0, feedSize);
        Mockito.verify(redisCache, Mockito.times(1)).getFromHSetCache(post, postId);
        Mockito.verify(redisCache, Mockito.times(1)).getFromHSetCache(infoAboutAuthor, authorId.toString());
        Mockito.verify(redisCache, Mockito.times(1)).getAllZSetValues(comment + postId);
        Mockito.verify(redisCache, Mockito.times(1)).getZSetSize(comment + postId);
    }

    @Test
    public void testCreateFeedFromDB() throws JsonProcessingException {
        Long userId = 1L;
        Long likeInfo = 10L;
        Long startPostId = null;
        List<Long> postIds = Arrays.asList(101L, 102L);
        String authorInfo = "{\"name\": \"authorName\"}";
        String postInfo = "{\"content\": \"postContent\"}";
        Set<String> commentInfo = Set.of("comment1", "comment2");
        Mockito.when(postService.findPostIdsByFolloweeId(userId, startPostId)).thenReturn(postIds);
        Mockito.when(postService.getPost(101L)).thenReturn(new PostDto());
        Mockito.when(postService.getPost(102L)).thenReturn(new PostDto());
        Mockito.when(userServiceClient.getUserByPostId(101L)).thenReturn(new UserDto());
        Mockito.when(userServiceClient.getUserByPostId(102L)).thenReturn(new UserDto());
        Mockito.when(commentService.getTheLastCommentsForNewsFeed(101L)).thenReturn(commentInfo);
        Mockito.when(commentService.getTheLastCommentsForNewsFeed(102L)).thenReturn(commentInfo);
        Mockito.when(likeService.getNumberOfLike(101L)).thenReturn(likeInfo);
        Mockito.when(likeService.getNumberOfLike(102L)).thenReturn(likeInfo);
        Mockito.when(objectMapper.writeValueAsString(Mockito.any())).thenReturn(postInfo).thenReturn(authorInfo);
        List<FeedDto> result = feedService.createFeedFromDB(userId, startPostId);
        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.size());
        FeedDto feedDto = result.get(0);
        Assertions.assertEquals(postInfo, feedDto.getPostInfo());
        Assertions.assertEquals(authorInfo, feedDto.getAuthorInfo());
        Assertions.assertEquals(commentInfo, feedDto.getCommentInfo());
        Assertions.assertEquals(likeInfo, feedDto.getLikeInfo());
        Mockito.verify(postService, Mockito.times(1)).findPostIdsByFolloweeId(userId, startPostId);
        Mockito.verify(commentService, Mockito.times(2)).getTheLastCommentsForNewsFeed(Mockito.anyLong());
        Mockito.verify(likeService, Mockito.times(2)).getNumberOfLike(Mockito.anyLong());
        Mockito.verify(objectMapper, Mockito.times(4)).writeValueAsString(Mockito.any());
    }

    @Test
    public void testHeatUserFeed() {
        Long userId = 1L;
        Long pivotPostId = 10L;
        List<Long> postIds = Arrays.asList(10L, 11L);
        Mockito.when(postService.findPostIdsByFolloweeId(1L, 10L)).thenReturn(postIds);
        Mockito.doNothing().when(heaterList.get(0)).addInfoToRedis(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doNothing().when(heaterList.get(1)).addInfoToRedis(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doNothing().when(heaterList.get(2)).addInfoToRedis(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doNothing().when(heaterList.get(3)).addInfoToRedis(Mockito.anyLong(), Mockito.anyLong());
        Mockito.doNothing().when(heaterList.get(4)).addInfoToRedis(Mockito.anyLong(), Mockito.anyLong());
        feedService.heatUserFeed(userId, pivotPostId);
        Mockito.verify(postService, Mockito.times(1)).findPostIdsByFolloweeId(userId, pivotPostId);
        Mockito.verify(postProducer, Mockito.times(1)).addInfoToRedis(userId, pivotPostId);
        Mockito.verify(commentProducer, Mockito.times(1)).addInfoToRedis(userId, pivotPostId);
        Mockito.verify(likeProducer, Mockito.times(1)).addInfoToRedis(userId, pivotPostId);
        Mockito.verify(authorProducer, Mockito.times(1)).addInfoToRedis(userId, pivotPostId);
        Mockito.verify(feedProducer, Mockito.times(1)).addInfoToRedis(userId, pivotPostId);
    }
}