package faang.school.postservice.service.feed;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Sql(scripts = "/db/script/feed_insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/db/script/feed_cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class FeedServiceIntTest {
    @Autowired
    private FeedService feedService;
    @Autowired
    private UserContext userContext;
    @MockBean
    private UserService userService;

    private static final int MAX_COMMENT = 3;
    private static final int POST_BATCH = 2;
    private static final long USER_ID = 1;
    private static final long AUTHOR_ID_8 = 8;
    private static final long AUTHOR_ID_10 = 10;

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

    @BeforeEach
    void setUp() {
        userContext.setUserId(USER_ID);
        UserRedis userRedis = new UserRedis();
        userRedis.setId(USER_ID);
        when(userService.toUserRedis(any(), any())).thenReturn(userRedis);
        when(userService.getSubs(USER_ID)).thenReturn(List.of(AUTHOR_ID_8, AUTHOR_ID_10));
    }

    @Test
    @DisplayName("Успешное получение фида - первые batch постов")
    void positive_whenPostIdLastNull_shouldReturnsFeed() {
        long firstPost = 2;
        long lastPost = 5;
        long firstComment = 6;
        long lastComment = 4;

        List<FeedDto> actual = feedService.getFeed(null, POST_BATCH);

        assertFalse(actual.isEmpty());
        assertEquals(2, actual.size());
        assertEquals(firstPost, actual.get(0).postId());
        assertEquals(lastPost, actual.get(1).postId());
        assertEquals(MAX_COMMENT, actual.get(0).comments().size());
        assertEquals(firstComment, actual.get(0).comments().get(0).id());
        assertEquals(lastComment, actual.get(0).comments().get(2).id());
    }

    @Test
    @DisplayName("Успешное получение фида - batch после конкретного поста")
    void positive_whenPostIdLastNotNull_shouldReturnsFeed() {
        long firstPost = 5;
        long lastPost = 4;

        List<FeedDto> actual = feedService.getFeed(2L, POST_BATCH);

        assertFalse(actual.isEmpty());
        assertEquals(2, actual.size());
        assertEquals(firstPost, actual.get(0).postId());
        assertEquals(lastPost, actual.get(1).postId());
    }
}