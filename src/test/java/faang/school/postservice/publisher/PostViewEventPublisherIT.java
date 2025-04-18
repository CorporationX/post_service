package faang.school.postservice.publisher;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.PostController;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifiedStatus;
import faang.school.postservice.repository.PostRepository;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Testcontainers
public class PostViewEventPublisherIT {

    @Autowired
    private PostController postController;
    @Autowired
    private UserContext userContext;
    @Autowired
    private PostRepository postRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Test
    public void testPositivePostViewEventPublisher() throws IOException {
        try (Jedis jedis = new Jedis(REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379))) {
            MyPubSub myPubSub = new MyPubSub();

            Thread subscriber = new Thread(() -> jedis.subscribe(myPubSub, "PostViewEvent_topic"));
            subscriber.start();

            Post post = Post.builder()
                    .content("Это пример поста.")
                    .authorId(1L)
                    .projectId(2L)
                    .likes(new ArrayList<>()) // Если у вас есть список лайков
                    .comments(new ArrayList<>()) // Если у вас есть список комментариев
                    .albums(new ArrayList<>()) // Если у вас есть список альбомов
                    .resources(new ArrayList<>()) // Если у вас есть список ресурсов
                    .published(true)
                    .publishedAt(LocalDateTime.now())
                    .scheduledAt(null) // Если у вас нет запланированной даты, установите null
                    .deleted(false)
                    .verifiedStatus(VerifiedStatus.APPROVED) // Установите статус верификации
                    .build();
            postRepository.save(post);
            userContext.setUserId(2L);

            PostViewEvent postViewEvent = PostViewEvent.builder()
                    .postId(post.getId())
                    .userId(userContext.getUserId())
                    .authorId(post.getAuthorId())
                    .build();

            PostDto postDto = postController.getPost(post.getId());

        }

    }
    @Test
    public void testNegativePostViewEventPublisher() throws IOException {

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
    }
}
