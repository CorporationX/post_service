package faang.school.postservice.publisher;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.redis.RedisConfig;
import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifiedStatus;
import faang.school.postservice.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
public class PostViewEventPublisherIT {

    @Autowired
    private PostController postController;
    @Autowired
    private UserContext userContext;
    @Autowired
    private PostRepository postRepository;
    private static final Logger log = LoggerFactory.getLogger(PostViewEventPublisherIT.class);

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Transactional
    @Test
    public void testPositivePostViewEventPublisher() {
        try (Jedis jedis = new Jedis(REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379))) {

            MyPubSub myPubSub = new MyPubSub();

            Thread subscriber = new Thread(() -> jedis.subscribe(myPubSub, "PostViewEvent_topic"));
            subscriber.start();

            Post post = Post.builder()
                    .content("Это пример поста.")
                    .authorId(1L)
                    .projectId(2L)
                    .likes(new ArrayList<>())
                    .comments(new ArrayList<>())
                    .albums(new ArrayList<>())
                    .resources(new ArrayList<>())
                    .published(true)
                    .publishedAt(LocalDateTime.now())
                    .scheduledAt(null)
                    .deleted(false)
                    .verifiedStatus(VerifiedStatus.APPROVED)
                    .build();
            postRepository.save(post);
            userContext.setUserId(2L);

            PostViewEvent postViewEvent = PostViewEvent.builder()
                    .postId(post.getId())
                    .userId(userContext.getUserId())
                    .authorId(post.getAuthorId())
                    .build();

            postController.getPost(post.getId());
            System.out.println(myPubSub.getReceivedMessage());
            log.info("Received message: {}", myPubSub.getReceivedMessage());

            assertTrue(myPubSub.getReceivedMessage().contains(postViewEvent.getPostId().toString()));
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
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    public static class MyPubSub extends JedisPubSub {
        private String receivedMessage;

        @Override
        public void onMessage(String channel, String message) {
            receivedMessage = message;
        }

        public String getReceivedMessage() {
            return receivedMessage;
        }
    }
}
