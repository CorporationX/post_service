package faang.school.postservice.producer;

import faang.school.postservice.config.KafkaConsumerTestConfig;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.PostNewEvent;
import faang.school.postservice.util.JsonMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Import(KafkaConsumerTestConfig.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class PostNewProducerIntTest {
    @Autowired
    private PostNewProducer producer;
    @SpyBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaConsumer<String, String> consumer;
    @Autowired
    private KafkaProperty kafkaProps;
    @Autowired
    private JsonMapper jsonMapper;
    @Value("${post.followersBatch:3}")
    private int followersBatch;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Успешная отправка NewPostEvent в Kafka")
    void positive_producerNewPostEventSent() {
        consumer.subscribe(List.of(kafkaProps.topic().postNew()));
        PostNewEvent event = createEvent();

        producer.sendEvent(event);

        ConsumerRecords<String, String> records = KafkaTestUtils.getRecords(consumer, Duration.ofMillis(5000));
        ConsumerRecord<String, String> record = records.records(kafkaProps.topic().postNew()).iterator().next();
        PostNewEvent actual = jsonMapper.fromJson(record.value(), PostNewEvent.class);

        verify(kafkaTemplate, times(2)).send(anyString(), anyString());
        assertEquals(followersBatch, actual.getFollowees().size());
    }

    private PostNewEvent createEvent() {
        return PostNewEvent.builder()
                .postId(1)
                .followees(List.of(1L, 2L, 3L, 4L, 5L, 6L))
                .publishedAt(LocalDateTime.now())
                .build();
    }
}