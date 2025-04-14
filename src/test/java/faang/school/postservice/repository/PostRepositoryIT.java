package faang.school.postservice.repository;

import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.VerifiedStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers
public class PostRepositoryIT {

    @Autowired
    private PostRepository postRepository;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @Test
    @DisplayName("Test find author ids with min rejected posts request")
    void testPositiveFindData() {
        int postsValue = 1;
        Long authorId = 1L;
        postRepository.save(createPost(authorId, VerifiedStatus.REJECTED));
        postRepository.save(createPost(2L, VerifiedStatus.PENDING));

        List<Long> authorIds = postRepository.findAuthorIdsWithMinRejectedPosts(postsValue);

        assertEquals(1, authorIds.size());
        assertEquals(authorId, authorIds.get(0));
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

    private Post createPost(Long authorId, VerifiedStatus status) {
        return Post.builder()
                .authorId(authorId)
                .verifiedStatus(status)
                .content("some content")
                .build();
    }
}
