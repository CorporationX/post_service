package faang.school.postservice.service.cache;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.dto.post.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class FeedCacheServiceIntTest {
    @Autowired
    private FeedCacheService service;
    @Autowired
    private RedisTemplate<String, Long> redisTemplate;

    private static final long USER_ID = 1L;
    private static final long POST_ID_1 = 1L;
    private static final long POST_ID_2 = 2L;
    private static final long POST_ID_3 = 3L;
    private static final int POST_BATCH = 2;
    private static final String FEED_KEY = "feed:";
    private static final String POSTS_KEY_SUFFIX = ":posts";
    private static final String KEY_SET = FEED_KEY + USER_ID + POSTS_KEY_SUFFIX;

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
    }

    @Test
    @DisplayName("Успешное добавление поста в кэш")
    void positive_shouldAddPostToCache() {
        ZSetOperations<String, Long> redisSet = redisTemplate.opsForZSet();

        service.addPost(POST_ID_1, LocalDateTime.now(), List.of(USER_ID));

        assertNotNull(redisSet.rank(KEY_SET, POST_ID_1));
    }

    @Test
    @DisplayName("Успешное добавление поста в кэш")
    void positive_shouldAddOnlyOnePostToCache() {
        ZSetOperations<String, Long> redisSet = redisTemplate.opsForZSet();

        service.addPost(POST_ID_1, LocalDateTime.now(), List.of(USER_ID));
        service.addPost(POST_ID_1, LocalDateTime.now(), List.of(USER_ID));

        assertNotNull(redisSet.rank(KEY_SET, POST_ID_1));
        assertEquals(1, redisSet.size(KEY_SET));
    }

    @Test
    @DisplayName("Успешное добавление списка постов в кэш")
    void positive_shouldAddPostsToCache() {
        ZSetOperations<String, Long> redisSet = redisTemplate.opsForZSet();
        List<PostDto> posts = List.of(createPostDto(POST_ID_1, 1), createPostDto(POST_ID_2, 2));

        service.addPosts(USER_ID, posts);

        assertNotNull(redisSet.rank(KEY_SET, POST_ID_1));
        assertNotNull(redisSet.rank(KEY_SET, POST_ID_2));
        assertEquals(posts.size(), redisSet.size(KEY_SET));
    }

    @Test
    @DisplayName("Успешное получение пачки постов из кэш")
    void positive_returnsNextPosts() {
        List<PostDto> posts = List.of(
                createPostDto(POST_ID_1, 1),
                createPostDto(POST_ID_2, 2),
                createPostDto(POST_ID_3, 3));
        service.addPosts(USER_ID, posts);

        Set<Long> actual = service.getNextPosts(USER_ID, null, POST_BATCH);

        assertEquals(POST_BATCH, actual.size());
        assertTrue(actual.contains(POST_ID_2));
        assertTrue(actual.contains(POST_ID_3));
    }

    @Test
    @DisplayName("Успешное получение пачки постов из кэш после конкретного поста")
    void positive_returnsNextPostsStartsAfter() {
        List<PostDto> posts = List.of(
                createPostDto(POST_ID_1, 1),
                createPostDto(POST_ID_2, 2),
                createPostDto(POST_ID_3, 3));
        service.addPosts(USER_ID, posts);

        Set<Long> actual = service.getNextPosts(USER_ID, POST_ID_3, POST_BATCH);

        assertEquals(POST_BATCH, actual.size());
        assertTrue(actual.contains(POST_ID_1));
        assertTrue(actual.contains(POST_ID_2));
    }

    // -----------------

    private PostDto createPostDto(long postId, int plusMinutes) {
        return PostDto.builder()
                .id(postId)
                .publishedAt(LocalDateTime.now().plusMinutes(plusMinutes))
                .build();
    }
}