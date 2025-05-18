package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.KafkaTestConfig;
import faang.school.postservice.config.RedisConfig;
import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.service.feed.FeedServiceImpl;
import faang.school.postservice.service.feed.comment.FeedCommentRedisService;
import faang.school.postservice.service.feed.comment.FeedCommentRedisServiceImpl;
import faang.school.postservice.service.feed.post.FeedPostRedisService;
import faang.school.postservice.service.feed.post.FeedPostRedisServiceImpl;
import faang.school.postservice.service.kafka.publisher.KafkaPublisher;
import faang.school.postservice.utils.JsonUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        classes = {
                FeedPostRedisService.class,
                FeedPostRedisServiceImpl.class,
                FeedCommentRedisService.class,
                FeedCommentRedisServiceImpl.class,
                FeedController.class,
                JsonUtils.class,
                RedisConfig.class,
                ObjectMapper.class,
                FeedServiceImpl.class,
                KafkaPublisher.class,
                KafkaTestConfig.class
        }
)
@ImportAutoConfiguration({
        RedisAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@EntityScan(basePackages = "faang.school.postservice.model")
@AutoConfigureMockMvc
@Testcontainers
@EnableJpaRepositories(basePackages = "faang.school.postservice.repository")
public class FeedControllerTest {

    @MockBean
    private UserServiceClient userServiceClient;

    @MockBean
    private ProjectServiceClient projectServiceClient;

    @SpyBean
    private PostRepository postRepository;

    @SpyBean
    private CommentRepository commentRepository;

    @Autowired
    private FeedPostRedisService feedPostRedisService;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private JsonUtils jsonUtils;

    @Autowired
    private FeedController feedController;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FeedService feedService;

    @Autowired
    private KafkaPublisher kafkaPublisher;

    @SpyBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private ExecutorService executor;

    @Container
    public static final KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:latest")).withExposedPorts(9093);


    @Container
    public static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2.6-alpine")
            .withExposedPorts(6379);

    @Container
    static final PostgreSQLContainer<?> postgresContainer =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("test")
                    .withUsername("test")
                    .withPassword("test");

    static {
        redisContainer.start();
        kafkaContainer.start();
        postgresContainer.start();
    }

    @DynamicPropertySource
    static void registerRedisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redisContainer::getHost);
        registry.add("spring.data.redis.port", redisContainer::getFirstMappedPort);

        registry.add("app.feed.post-batch-size", () -> 1);
        registry.add("app.feed.comment-batch-size", () -> 1);

        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
        registry.add("spring.datasource.driver-class-name", postgresContainer::getDriverClassName);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    private final Long userId = 1L;
    private final Long postId = 1L;
    private final Long commentId = 3L;
    private final int offset = 0;
    private FeedPostDto feedPostDto;
    private FeedCommentDto feedCommentDto;
    private Post post;
    private Comment comment;
    private final String heatTopic = "feed-heat-topic";

    @BeforeEach
    void init() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(feedController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

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

        commentRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    public void testGetFeedPosts_takeFromCache() throws Exception {
        redisTemplate.opsForList().rightPushAll(getUserFeedPostsKey(userId, offset),
                String.valueOf(feedPostDto.getId()));

        String postKey = getPostKey(feedPostDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedPostDto);
        redisTemplate.opsForHash().putAll(postKey, json);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/feed/posts/user/{userId}", userId)
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk());

        verify(postRepository, never()).findPublishedPostsByAuthorIds(any(), any());
    }

    @Test
    public void testGetFeedPosts_takeFromDataBase() throws Exception {
        postRepository.save(post);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/feed/posts/user/{userId}", userId)
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk());

        verify(postRepository, times(1)).findPublishedPostsByAuthorIds(any(), any());
    }

    @Test
    public void testGetFeedComments_takeFromCache() throws Exception {
        redisTemplate.opsForList().rightPushAll(getPostFeedCommentsKey(userId, offset),
                String.valueOf(feedCommentDto.getId()));

        String commentKey = getCommentKey(feedCommentDto.getId());
        Map<String, String> json = jsonUtils.toMap(feedCommentDto);
        redisTemplate.opsForHash().putAll(commentKey, json);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/feed/comments/post/{postId}", postId)
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk());

        verify(commentRepository, never()).findByPostIdOrderByCreatedAtDesc(any(), any());
    }

    @Test
    public void testGetFeedComments_takeFromDataBase() throws Exception {
        post = postRepository.save(post);
        comment.setPost(post);
        comment = commentRepository.save(comment);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/feed/comments/post/{postId}", postId)
                        .param("offset", String.valueOf(offset)))
                .andExpect(status().isOk());

        verify(commentRepository, times(1)).findByPostIdOrderByCreatedAtDesc(any(), any());
    }

    @Test
    public void testHeatFeedCache() throws Exception {
        when(userServiceClient.getUserIdsByPage(anyInt(), anyInt()))
                .thenReturn(List.of(1L, 2L))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/feed/heat"))
                .andExpect(status().isAccepted());

        verify(kafkaTemplate, times(1)).send(heatTopic, String.valueOf(1L));
        verify(kafkaTemplate, times(1)).send(heatTopic, String.valueOf(2L));
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
