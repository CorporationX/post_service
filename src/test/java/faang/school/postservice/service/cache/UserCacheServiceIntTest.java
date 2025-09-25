package faang.school.postservice.service.cache;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.redis.UserRedisRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class UserCacheServiceIntTest {
    @Autowired
    private UserCacheService service;
    @Autowired
    private UserRedisRepository repository;

    private static final long USER_ID = 1L;
    private static final long PROJECT_ID = 2L;
    private static final long POST_ID = 1L;

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

    @Test
    @DisplayName("Успешное получение автора из кэша")
    void positive_returnsAuthor() {
        String expected = "username";
        UserRedis user = createUserRedis(USER_ID, expected);
        repository.save(user);

        Optional<String> actual = service.findAuthor(USER_ID, null);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Успешное сохранение автора в кэш")
    void positive_shouldSaveUserToCache() {
        service.save(createUserRedis(PROJECT_ID, "project title"));

        Optional<UserRedis> actual = repository.findById(PROJECT_ID);

        assertTrue(actual.isPresent());
        assertEquals(PROJECT_ID, actual.get().getId());
    }

    // ------------------

    private UserRedis createUserRedis(long authorId, String username) {
        UserRedis user = new UserRedis();
        user.setId(authorId);
        user.setUsername(username);
        return user;
    }
}