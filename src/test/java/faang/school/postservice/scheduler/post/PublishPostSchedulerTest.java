package faang.school.postservice.scheduler.post;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class PublishPostSchedulerTest {
    private static final long TEST_SCHEDULER_INTERVAL = 3000;
    private static final String TEST_SCHEDULER_CHRON = "0/3 * * * * *";

    @Autowired
    private PublishPostScheduler publishPostScheduler;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Container
    public static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER
            = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws InterruptedException {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("scheduled-publication.cron", () -> TEST_SCHEDULER_CHRON);
        Thread.sleep(1000);
    }

    @Test
    @Sql("/db/scheduler/post/publish/insert_initial_posts.sql")
    void testPublishScheduledPosts() throws InterruptedException {
        checkAllPublished();
        jdbcTemplate.execute
                (
                        "insert into post (content, author_id, published, scheduled_at, deleted) " +
                                "values ('Content', 1, false, '1990-12-31 10:10:00+00', false);"
                );
        checkAllPublished();
    }

    private void checkAllPublished() throws InterruptedException {
        Thread.sleep(TEST_SCHEDULER_INTERVAL);
        List<Post> posts = IterableUtils.toList(postRepository.findAll());
        posts.forEach(post -> {
            assertTrue(post.isPublished());
            assertNotNull(post.getPublishedAt());
            assertNotNull(post.getUpdatedAt());
        });
    }
}