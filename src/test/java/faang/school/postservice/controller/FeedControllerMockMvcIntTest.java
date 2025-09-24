package faang.school.postservice.controller;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.PostServiceApp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {PostServiceApp.class,
        FeedControllerMockMvcIntTest.RedisTestConfiguration.class})
@Testcontainers
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FeedControllerMockMvcIntTest {

    @Autowired
    protected MockMvc mockMvc;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse(
                    "redis/redis-stack:latest"));

    @DynamicPropertySource
    static void setContainerProperties(DynamicPropertyRegistry registry) {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();

        registry.add("spring.datasource.url",
                POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username",
                POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password",
                POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port",
                () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(3_000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Redis container running: " + REDIS_CONTAINER.isRunning());
        System.out.println("Redis container host: " + REDIS_CONTAINER.getHost());
        System.out.println("Redis container port: " + REDIS_CONTAINER.getMappedPort(6379));
    }

    @BeforeAll
    static void setUp() {
        REDIS_CONTAINER.start();
        System.out.println("Redis container started: " + REDIS_CONTAINER.isRunning());
    }

    @Test
    void testGetFeedWithoutLastPostId() throws Exception {
        mockMvc.perform(
                        get("/api/feed")
                                .header("x-user-id", 999)
                )
                .andExpect(status().isOk());
    }

    @TestConfiguration
    static class RedisTestConfiguration {

        @Bean
        @Primary // Важно для переопределения существующего бина
        public JedisConnectionFactory testRedisConnectionFactory() {
            // Получаем адрес и порт из контейнера
            RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
            redisConfig.setHostName(REDIS_CONTAINER.getHost());
            redisConfig.setPort(REDIS_CONTAINER.getMappedPort(6379));

            return new JedisConnectionFactory(redisConfig);
        }

        @DependsOn("testRedisConnectionFactory")
        @Bean
        @Primary
        public RedisTemplate<Long, Object> testRedisTemplate(JedisConnectionFactory connectionFactory) {
            RedisTemplate<Long, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(connectionFactory);
            // Здесь можно настроить сериализаторы, если необходимо
            return redisTemplate;
        }
    }
}
