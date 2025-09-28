package faang.school.postservice.producer;

import faang.school.postservice.config.KafkaConsumerTestConfig;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.FeedHeatEvent;
import faang.school.postservice.util.JsonMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@Import(KafkaConsumerTestConfig.class)
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class FeedHeatProducerIntTest {
    @Autowired
    private FeedHeatProducer producer;
    @SpyBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaConsumer<String, String> consumer;
    @Autowired
    private KafkaProperty kafkaProps;
    @Autowired
    private JsonMapper jsonMapper;

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
    @DisplayName("Успешная отправка FeedHeatEvent в Kafka")
    void positive_producerFeedHeatEventSent() {
        consumer.subscribe(List.of(kafkaProps.topic().feedHeat()));
        FeedHeatEvent event = createEvent();
        String expected = jsonMapper.toJson(event);

        producer.sendEvent(event);

        ConsumerRecord<String, String> record =
                KafkaTestUtils.getSingleRecord(consumer, kafkaProps.topic().feedHeat(), Duration.ofMillis(5000));

        assertEquals(expected, record.value());
    }

    private FeedHeatEvent createEvent() {
        return FeedHeatEvent.builder()
                .userId(1)
                .posts(Collections.emptyList())
                .build();
    }
}