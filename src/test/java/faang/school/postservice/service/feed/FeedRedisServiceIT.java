package faang.school.postservice.service.feed;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.RedisConfig;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.utils.JsonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(
        classes = {
                FeedRedisService.class,
                FeedRedisServiceImpl.class,
                JsonUtils.class,
                RedisConfig.class,
                ObjectMapper.class
        }
)
@ImportAutoConfiguration(RedisAutoConfiguration.class)
@Testcontainers
public class FeedRedisServiceIT {

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private ProjectServiceClient projectServiceClient;

    @MockBean
    private PostRepository postRepository;

    @MockBean
    private CommentRepository commentRepository;

    @SpyBean
    private PostMapperImpl postMapper;

    @SpyBean
    private CommentMapperImpl commentMapper;

    @Autowired
    private FeedRedisService feedRedisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JsonUtils jsonUtils;

    @Container
    public static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2.6-alpine")
            .withExposedPorts(6379);

    static {
        redisContainer.start();
    }

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);
        registry.add("app.feed.post-batch-size", () -> 1);
        registry.add("app.feed.comment-batch-size", () -> 1);
    }

    private final Long userId = 1L;
    private final Long postId = 1L;
    private final Long commentId = 3L;
    private final int offset = 0;
    private final UserDto userDto = new UserDto(1L, "name", "mail");
    private FeedPostDto feedPostDto;
    private FeedCommentDto feedCommentDto;
    private Post post;
    private Comment comment;

    @BeforeEach
    void init() {
        feedPostDto = FeedPostDto.builder()
                .id(postId)
                .authorId(1L)
                .authorName("test")
                .views(1)
                .likes(2)
                .build();

        feedCommentDto = FeedCommentDto.builder()
                .id(commentId)
                .authorId(1L)
                .postId(postId)
                .likes(2)
                .content("test")
                .build();

        post = Post.builder()
                .id(postId)
                .content("content1")
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .authorId(1L)
                .build();

        comment = Comment.builder()
                .id(commentId)
                .content("test")
                .likes(new ArrayList<>(List.of(new Like(), new Like())))
                .authorId(1L)
                .post(post)
                .build();
    }

    @AfterEach
    void tearDown() {
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.serverCommands().flushDb();
            return null;
        });
    }

    @Test
    public void testIsPostAvailableInCache_postAvailable() {
        redisTemplate.opsForList().rightPushAll(getUserFeedPostsKey(userId, offset),
                String.valueOf(feedPostDto.getId()));

        String postKey = getPostKey(feedPostDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedPostDto);
        redisTemplate.opsForHash().putAll(postKey, json);

        assertTrue(feedRedisService.isPostAvailableInCache(userId, offset));
    }

    @Test
    public void testIsPostAvailableInCache_detailsAbsent_presentInList() {
        redisTemplate.opsForList().rightPushAll(getUserFeedPostsKey(userId, offset),
                String.valueOf(feedPostDto.getId()));

        assertFalse(feedRedisService.isPostAvailableInCache(userId, offset));
    }

    @Test
    public void testIsPostAvailableInCache_detailsPresent_absentInList() {
        String postKey = getPostKey(feedPostDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedPostDto);
        redisTemplate.opsForHash().putAll(postKey, json);

        assertFalse(feedRedisService.isPostAvailableInCache(userId, offset));
    }

    @Test
    public void testIsPostAvailableInCache_postAbsent() {
        assertFalse(feedRedisService.isPostAvailableInCache(userId, offset));
    }

    @Test
    public void testIsCommentAvailableInCache_commentAvailable() {
        redisTemplate.opsForList().rightPushAll(getPostFeedCommentsKey(postId, offset),
                String.valueOf(feedCommentDto.getId()));

        String commentKey = getCommentKey(feedCommentDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedCommentDto);
        redisTemplate.opsForHash().putAll(commentKey, json);

        assertTrue(feedRedisService.isCommentAvailableInCache(postId, offset));
    }

    @Test
    public void testIsCommentAvailableInCache_detailsAbsent_presentInList() {
        redisTemplate.opsForList().rightPushAll(getPostFeedCommentsKey(postId, offset),
                String.valueOf(feedCommentDto.getId()));

        assertFalse(feedRedisService.isCommentAvailableInCache(postId, offset));
    }

    @Test
    public void testIsCommentAvailableInCache_detailsPresent_absentInList() {
        String commentKey = getCommentKey(feedCommentDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedCommentDto);
        redisTemplate.opsForHash().putAll(commentKey, json);

        assertFalse(feedRedisService.isCommentAvailableInCache(postId, offset));
    }

    @Test
    public void testIsCommentAvailableInCache_commentAbsent() {
        assertFalse(feedRedisService.isCommentAvailableInCache(postId, offset));
    }

    @Test
    public void testLoadPostsFromCache_returnsResult() {
        redisTemplate.opsForList().rightPushAll(getUserFeedPostsKey(userId, offset),
                String.valueOf(feedPostDto.getId()));

        String postKey = getPostKey(feedPostDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedPostDto);
        redisTemplate.opsForHash().putAll(postKey, json);

        List<FeedPostDto> result = feedRedisService.loadPostsFromCache(userId, offset);

        assertEquals(1, result.size());
        assertEquals(feedPostDto, result.get(0));
    }

    @Test
    public void testLoadCommentsFromCache_returnsResult() {
        redisTemplate.opsForList().rightPushAll(getPostFeedCommentsKey(postId, offset),
                String.valueOf(feedCommentDto.getId()));

        String commentKey = getCommentKey(feedCommentDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedCommentDto);
        redisTemplate.opsForHash().putAll(commentKey, json);

        List<FeedCommentDto> result = feedRedisService.loadCommentsFromCache(userId, offset);

        assertEquals(1, result.size());
        assertEquals(feedCommentDto, result.get(0));
    }

    @Test
    public void testCachePostDetailsIdForUser_saved() {
        feedRedisService.cachePostIdForUser(userId, postId, 0);

        assertTrue(redisTemplate.hasKey(getUserFeedPostsKey(userId, offset)));
    }

    @Test
    public void testCacheCommentDetailsIdForPost_saved() {
        feedRedisService.cacheCommentIdForPost(postId, commentId, 0);

        assertTrue(redisTemplate.hasKey(getPostFeedCommentsKey(postId, offset)));
    }

    @Test
    public void testCachePostDetails_saved() {
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);

        feedRedisService.cachePostDetails(feedPostDto);

        assertTrue(redisTemplate.hasKey(getPostKey(feedPostDto.getId())));
    }

    @Test
    public void testRemovePostFromCache_removed() {
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);

        feedRedisService.cachePostDetails(feedPostDto);
        assertTrue(redisTemplate.hasKey(getPostKey(feedPostDto.getId())));

        feedRedisService.removePostFromCache(feedPostDto.getId());
        assertFalse(redisTemplate.hasKey(getPostKey(feedPostDto.getId())));
    }

    @Test
    public void testIncrementPostLikes() {
        feedRedisService.cachePostIdForUser(userId, postId, offset);
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);
        feedRedisService.cachePostDetails(feedPostDto);

        int was = feedRedisService.loadPostsFromCache(userId, offset).get(0).getLikes();
        feedRedisService.incrementPostLikes(feedPostDto.getId());

        assertEquals(was + 1, feedRedisService.loadPostsFromCache(userId, offset).get(0).getLikes());
    }

    @Test
    public void testDecrementPostLikes() {
        feedRedisService.cachePostIdForUser(userId, postId, offset);
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);
        feedRedisService.cachePostDetails(feedPostDto);

        int was = feedRedisService.loadPostsFromCache(userId, offset).get(0).getLikes();
        feedRedisService.decrementPostLikes(feedPostDto.getId());

        assertEquals(was - 1, feedRedisService.loadPostsFromCache(userId, offset).get(0).getLikes());
    }

    @Test
    public void testIncrementCommentLikes() {
        feedRedisService.cacheCommentIdForPost(postId, feedCommentDto.getId(), offset);
        feedRedisService.cacheCommentDetails(feedCommentDto);

        int was = feedRedisService.loadCommentsFromCache(postId, offset).get(0).getLikes();
        feedRedisService.incrementCommentLikes(feedCommentDto.getId());

        assertEquals(was + 1, feedRedisService.loadCommentsFromCache(postId, offset).get(0).getLikes());
    }

    @Test
    public void testDecrementCommentLikes() {
        feedRedisService.cacheCommentIdForPost(postId, feedCommentDto.getId(), offset);
        feedRedisService.cacheCommentDetails(feedCommentDto);

        int was = feedRedisService.loadCommentsFromCache(postId, offset).get(0).getLikes();
        feedRedisService.decrementCommentLikes(feedCommentDto.getId());

        assertEquals(was - 1, feedRedisService.loadCommentsFromCache(postId, offset).get(0).getLikes());
    }

    @Test
    public void testIncrementPostComments() {
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);
        feedRedisService.cachePostIdForUser(userId, feedPostDto.getId(), offset);
        feedRedisService.cachePostDetails(feedPostDto);

        int was = feedRedisService.loadPostsFromCache(userId, offset).get(0).getComments();
        feedRedisService.incrementPostComments(feedPostDto.getId());

        assertEquals(was + 1, feedRedisService.loadPostsFromCache(userId, offset).get(0).getComments());
    }

    @Test
    public void testDecrementPostComments() {
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);
        feedRedisService.cachePostIdForUser(userId, feedPostDto.getId(), offset);
        feedRedisService.cachePostDetails(feedPostDto);

        int was = feedRedisService.loadPostsFromCache(userId, offset).get(0).getComments();
        feedRedisService.decrementPostComments(feedPostDto.getId());

        assertEquals(was - 1, feedRedisService.loadPostsFromCache(userId, offset).get(0).getComments());
    }

    @Test
    public void testCacheCommentDetails() {
        feedRedisService.cacheCommentDetails(feedCommentDto);

        assertTrue(redisTemplate.hasKey(getCommentKey(feedCommentDto.getId())));
    }

    @Test
    public void testRemoveCommentFromCache_removed() {
        feedRedisService.cacheCommentDetails(feedCommentDto);
        assertTrue(redisTemplate.hasKey(getCommentKey(feedCommentDto.getId())));

        feedRedisService.removeCommentFromCache(feedCommentDto.getId());
        assertFalse(redisTemplate.hasKey(getCommentKey(feedCommentDto.getId())));
    }

    @Test
    public void testIncrementPostViews() {
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);
        feedRedisService.cachePostIdForUser(userId, feedPostDto.getId(), offset);
        feedRedisService.cachePostDetails(feedPostDto);

        int was = feedRedisService.loadPostsFromCache(userId, offset).get(0).getViews();
        feedRedisService.incrementPostViews(feedPostDto.getId());

        assertEquals(was + 1, feedRedisService.loadPostsFromCache(userId, offset).get(0).getViews());
    }

    @Test
    public void testPreloadUserPosts() {
        when(userServiceClient.getUser(feedPostDto.getAuthorId())).thenReturn(userDto);
        when(userServiceClient.getFollowees(userId)).thenReturn(List.of(10L));
        when(postRepository.findPublishedPostsByAuthorIds(any(Pageable.class), eq(List.of(10L))))
                .thenReturn(new PageImpl<>(List.of(post)));

        feedRedisService.preloadUserPosts(userId);

        assertTrue(redisTemplate.hasKey(getUserFeedPostsKey(userId, offset)));
        assertTrue(redisTemplate.hasKey(getPostKey(feedPostDto.getId())));
    }

    @Test
    public void testPreloadPostComments() {
        when(commentRepository.findByPostIdOrderByCreatedAtDesc(any(Pageable.class), eq(feedCommentDto.getPostId())))
                .thenReturn(new PageImpl<>(List.of(comment)));

        feedRedisService.preloadPostComments(feedCommentDto.getPostId());

        assertTrue(redisTemplate.hasKey(getPostFeedCommentsKey(postId, offset)));
        assertTrue(redisTemplate.hasKey(getCommentKey(feedCommentDto.getId())));
    }

    private String getPostKey(Long postId) {
        String postDetailsKey = "post:%d:";
        return postDetailsKey.formatted(postId);
    }

    private String getCommentKey(Long commentId) {
        String commentDetailsKey = "comment:%d:";
        return commentDetailsKey.formatted(commentId);
    }

    private String getUserFeedPostsKey(Long userId, int offset) {
        String userFeedPostsKey = "feed:posts:user:%d:offset:%d";
        return userFeedPostsKey.formatted(userId, offset);
    }

    private String getPostFeedCommentsKey(Long postId, int offset) {
        String postFeedCommentsKey = "feed:post:%d:comments:offset:%d";
        return postFeedCommentsKey.formatted(postId, offset);
    }
}
