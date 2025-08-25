package faang.school.postservice.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.KafkaConsumerTestConfig;
import faang.school.postservice.dto.event.CommentEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Import(KafkaConsumerTestConfig.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class CommentEventProducerIntTest {
    @Autowired
    private CommentEventProducer producer;
    @Autowired
    private ObjectMapper objectMapper;
    @SpyBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaConsumer<String, String> consumer;
    @Captor
    private ArgumentCaptor<String> topicCaptor;
    @Captor
    private ArgumentCaptor<String> dataCaptor;
    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");
    private final String topic = "comment.new";

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
    @DisplayName("Успешная отправка event в Kafka")
    void positive_producerCommentEventSent() throws JsonProcessingException {
        consumer.subscribe(List.of(topic));
        CommentEvent event = createCommentEvent();
        String expected = objectMapper.writeValueAsString(event);

        producer.sendEvent(event);

        verify(kafkaTemplate, times(1)).send(topicCaptor.capture(), dataCaptor.capture());
        String actual = dataCaptor.getValue();
        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumer, topic, Duration.ofMillis(5000));
        assertEquals(expected, actual);
        assertEquals(expected, record.value());
    }

    private CommentEvent createCommentEvent() {
        return CommentEvent.builder()
                .id(1)
                .postAuthorId(2)
                .commentAuthorId(3)
                .postId(4)
                .content("comment content")
                .build();
    }
}