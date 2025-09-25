package faang.school.postservice.service.cache;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class PostCacheServiceIntTest {
    @Autowired
    private PostCacheService service;
    @Autowired
    private PostRedisRepository repository;
    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private static final long POST_ID_1 = 1;
    private static final long POST_ID_2 = 2;
    private static final String FEED_KEY = "posts:";
    private static final String POSTS_KEY_SUFFIX = ":comments";
    private static final String KEY_SET = FEED_KEY + POST_ID_1 + POSTS_KEY_SUFFIX;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");
    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
    }

    @BeforeEach
    void setUp() {
        redisTemplate.delete(KEY_SET);

        PostRedis postRedis = new PostRedis();
        postRedis.setId(POST_ID_1);
        postRedis.setLikes(1);
        postRedis.setViews(1);
        repository.save(postRedis);
    }

    @Test
    @DisplayName("Успешное добавление лайка посту в кэш")
    void positive_shouldAddLikeToPostInCache() {
        service.like(POST_ID_1);

        long actual = repository.findById(POST_ID_1).get().getLikes();

        assertEquals(2, actual);
    }

    @Test
    @DisplayName("Успешное добавление просмотра посту в кэш")
    void positive_shouldAddViewToPostInCache() {
        service.view(POST_ID_1);

        long actual = repository.findById(POST_ID_1).get().getViews();

        assertEquals(2, actual);
    }

    @Test
    @DisplayName("Успешное добавление комментария посту в кэш")
    void positive_shouldAddCommentToPostInCache() {
        CommentDto comment = createCommentDto(1);

        service.addComment(POST_ID_1, comment);
        List<CommentDto> actual = service.getComments(POST_ID_1);

        assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("Успешное сохранение комментариев в кэш")
    void positive_shouldSaveLimitComments() {
        List<CommentDto> comments = List.of(createCommentDto(1),
                createCommentDto(2),
                createCommentDto(3),
                createCommentDto(4));

        service.saveComments(POST_ID_1, comments);
        List<CommentDto> actual = service.getComments(POST_ID_1);

        assertEquals(3, actual.size());
    }

    @Test
    @DisplayName("Успешное получение комментариев к посту из кэш")
    void positive_returnsCommentsByPostId() {
        List<CommentDto> comments = List.of(createCommentDto(1), createCommentDto(2), createCommentDto(3));
        service.saveComments(POST_ID_1, comments);

        List<CommentDto> actual = service.getComments(POST_ID_1);

        assertFalse(actual.isEmpty());
    }

    @Test
    @DisplayName("Успешное получение поста из кэш")
    void positive_returnsPostById() {
        Optional<PostRedis> actual = service.findById(POST_ID_1);

        assertTrue(actual.isPresent());
        assertEquals(POST_ID_1, actual.get().getId());
    }

    @Test
    @DisplayName("Успешное сохранение PostDto в кэш")
    void positive_shouldSavePostDtoInCache() {
        PostDto post = createPostDto(POST_ID_2);
        service.save(post);

        Optional<PostRedis> actual = service.findById(POST_ID_2);

        assertTrue(actual.isPresent());
        assertEquals(POST_ID_2, actual.get().getId());
    }

    @Test
    @DisplayName("Успешное сохранение Post в кэш")
    void positive_shouldSavePostInCache() {
        Post post = createPost(POST_ID_2);
        service.save(post);

        Optional<PostRedis> actual = service.findById(POST_ID_2);

        assertTrue(actual.isPresent());
        assertEquals(POST_ID_2, actual.get().getId());
    }

    // -------------------

    private CommentDto createCommentDto(long commentId) {
        return CommentDto.builder()
                .id(commentId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PostDto createPostDto(long postId) {
        return PostDto.builder()
                .id(postId)
                .build();
    }

    private Post createPost(long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }
}