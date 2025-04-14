package faang.school.postservice.publisher;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.config.redis.RedisConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifiedStatus;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.scheduler.ScheduledAuthorBanner;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Import(RedisConfig.class)
public class AuthorBanPublisherIT {

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Value("${spring.data.redis.channels.user-ban}")
    private String userBanTopic;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ScheduledAuthorBanner authorBanner;

    @Test
    void testPositivePublishAuthorBanEvent() {
        try (Jedis jedis = new Jedis(REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379))) {
            RedisPubSub redisPubSub = new RedisPubSub();
            Long authorId = 1L;
            int postCount = 5;

            for (int i = 0; i < postCount; i++) {
                postRepository.save(createPost(authorId));
            }
            Thread subscriberThread = new Thread(() -> jedis.subscribe(redisPubSub, userBanTopic));
            subscriberThread.start();

            authorBanner.checkAuthorsPostsVerification();

            Thread.sleep(1000);
            assertNotNull(redisPubSub.getReceivedMessage(), "No message received within timeout");
            assertTrue(redisPubSub.getReceivedMessage().contains(authorId.toString()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
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

    public static class RedisPubSub extends JedisPubSub {

        private String receivedMessage;

        public String getReceivedMessage() {
            return this.receivedMessage;
        }

        @Override
        public void onMessage(String channel, String message) {
            receivedMessage = message;
        }
    }

    private Post createPost(Long authorId) {
        return Post.builder()
                .authorId(authorId)
                .content("some content")
                .verifiedStatus(VerifiedStatus.REJECTED)
                .build();
    }
}
