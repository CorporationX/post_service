package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.CommentEventKafka;
import faang.school.postservice.redis.service.PostCacheService;
import faang.school.postservice.util.SharedTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class KafkaCommentProducerConsumerTest {

    @Value("${spring.kafka.topics.comment}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private PostCacheService postCacheService;

    @DynamicPropertySource
    static void overrideSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SharedTestContainers.POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", SharedTestContainers.POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", SharedTestContainers.POSTGRES_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", SharedTestContainers.POSTGRES_CONTAINER::getDriverClassName);
        registry.add("spring.liquibase.enabled", () -> false);
        registry.add("spring.data.redis.host", SharedTestContainers.REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", () -> SharedTestContainers.REDIS_CONTAINER.getMappedPort(6379));
        registry.add("feed-posts-per-request.size", () -> 2);
        registry.add("spring.kafka.bootstrap-servers", SharedTestContainers.KAFKA_CONTAINER::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {
        kafkaTemplate.flush();
    }


    @Test
    public void testProducerAndConsumer() throws InterruptedException {
        String key = "test-key-comment";
        CommentEventKafka testEvent =
                new CommentEventKafka(1L, 2L, 3L, "Test comment content", LocalDateTime.now());

        kafkaTemplate.send(topic, key, testEvent);

        Thread.sleep(2000);

        verify(postCacheService).updatePostComments(testEvent);
    }
}
