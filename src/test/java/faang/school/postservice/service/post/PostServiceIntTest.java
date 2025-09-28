package faang.school.postservice.service.post;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.producer.PostNewProducer;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import faang.school.postservice.service.user.UserService;
import faang.school.postservice.validator.OwnerValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
@Sql(scripts = "/db/script/post_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/db/script/post_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PostServiceIntTest {
    @Autowired
    private PostService service;
    @Autowired
    private PostRepository repository;
    @Autowired
    private PostRedisRepository postRedisRepository;
    @Autowired
    private UserRedisRepository userRedisRepository;
    @MockBean
    private OwnerValidator validator;
    @MockBean
    private PostNewProducer postNewProducer;
    @MockBean
    private UserService userService;

    private static final Long USER_ID = 6L;
    private static final long POST_ID = 1;
    private static final long AUTHOR_ID_8 = 8;
    private static final long AUTHOR_ID_10 = 10;
    private static final int POST_BATCH = 2;
    private static final String CONTENT = "post text";

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

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Успешное создание поста")
    void positive_shouldCreatePost() {
        PostDto postDto = createDto();

        PostDto dto = service.create(postDto);
        Optional<Post> actual = repository.findById(dto.id());

        assertTrue(repository.findById(dto.id()).isPresent());
        assertFalse(actual.get().isDeleted());
        assertFalse(actual.get().isPublished());
    }

    @Test
    @DisplayName("Успешная публикация поста")
    void positive_shouldPublishAndSavePostAndAuthorToCache() {
        UserRedis userRedis = new UserRedis();
        userRedis.setId(USER_ID);
        when(userService.toUserRedis(USER_ID, null)).thenReturn(userRedis);

        service.publish(POST_ID);

        Post actualPost = repository.findById(POST_ID).get();

        assertTrue(actualPost.isPublished());
        assertTrue(postRedisRepository.findById(POST_ID).isPresent());
        assertTrue(userRedisRepository.findById(USER_ID).isPresent());
    }

    @Test
    @DisplayName("Успешное получение пачки последних постов по подпискам")
    void positive_shouldReturnsRecentBatchByAuthors() {
        long firstPost = 2;
        long lastPost = 5;

        List<PostDto> actual = service.findRecentBatchByAuthors(List.of(AUTHOR_ID_8, AUTHOR_ID_10), null, POST_BATCH);

        assertFalse(actual.isEmpty());
        assertEquals(2, actual.size());
        assertEquals(firstPost, actual.get(0).id());
        assertEquals(lastPost, actual.get(1).id());
    }

    @Test
    @DisplayName("Успешное получение поста по id")
    void positive_shouldReturnsPostById() {
        PostDto actual = service.getById(POST_ID);

        assertNotNull(actual);
        assertEquals(POST_ID, actual.id());
    }

    @Test
    @DisplayName("Успешное получение свежей пачки любых постов")
    void positive_returnsAnyRecentPosts() {
        long firstPost = 2;
        long lastPost = 5;

        List<PostDto> actual = service.findAnyRecentBatch(POST_BATCH);

        assertFalse(actual.isEmpty());
        assertEquals(2, actual.size());
        assertEquals(firstPost, actual.get(0).id());
        assertEquals(lastPost, actual.get(1).id());
    }

    @Test
    @DisplayName("Ошибка получения поста по id - пост не найден")
    void negative_whenPostNotFoundById_throwsError() {
        String expectedMessage = "Post %d not found".formatted(-1);

        String actualMessage = assertThrows(EntityNotFoundException.class,
                () -> service.getById(-1L)).getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    @DisplayName("Ошибка получения свежей пачки любых постов - не найдены")
    @Sql(scripts = "/db/script/post_cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void negative_whenAnyPostsNotFound_throwsError() {
        assertThrows(EntityNotFoundException.class,
                () -> service.findAnyRecentBatch(POST_BATCH));
    }

    // -----------------

    private PostDto createDto() {
        return PostDto.builder()
                .authorId(USER_ID)
                .content(CONTENT)
                .build();
    }
}