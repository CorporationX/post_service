package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redis.testcontainers.RedisContainer;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.KafkaProducerConfig;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.kafkaevents.CommentEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.publisher.CommentEventPublisher;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@DirtiesContext
@Testcontainers
@Import(KafkaProducerConfig.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class CommentServiceIT {

    @MockBean
    private UserServiceClient client;

    @Autowired
    private ObjectMapper objectMapper;

    @SpyBean
    private CommentEventPublisher commentEventPublisher;

    @Autowired
    private KafkaConsumer<String, String> consumer;

    @Value("${spring.data.kafka.topic.comment}")
    private String topic;

    @Autowired
    private CommentService commentService;

    @Container
    private static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.6");

    @Container
    private static final RedisContainer REDIS_CONTAINER =
            new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"));

    @DynamicPropertySource
    static void start(DynamicPropertyRegistry registry) {
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

    @Sql(scripts = "/sql/test-posts.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Test
    void testCreateComment() throws JsonProcessingException {
        consumer.subscribe(Collections.singletonList(topic));
        when(client.getUser(any(Long.class))).thenReturn(createUserDto());
        CommentDto commentForCreate = createCommentDto();

        CommentDto result = commentService.createComment(2,2, commentForCreate);

        ArgumentCaptor<CommentEvent> argumentCaptor = ArgumentCaptor.forClass(CommentEvent.class);
        verify(commentEventPublisher, times(1)).publish(argumentCaptor.capture());
        CommentEvent commentEvent = argumentCaptor.getValue();

        assertEquals(commentForCreate.authorId(), result.authorId());
        assertEquals(commentForCreate.content(), result.content());

        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofMillis(5000));
        CommentEvent publishedEvent = objectMapper.readValue(record.value(), CommentEvent.class);

        assertEquals(commentEvent.id(), publishedEvent.id());
        assertEquals(commentEvent.content(), publishedEvent.content());
    }

    private UserDto createUserDto() {
        return UserDto.builder()
                .id(1L)
                .username("test")
                .email("test")
                .build();
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .authorId(1)
                .postId(2)
                .content("test")
                .build();
    }
}
