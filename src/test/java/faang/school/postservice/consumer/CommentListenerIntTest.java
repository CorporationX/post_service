package faang.school.postservice.consumer;

import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.service.cache.PostCacheService;
import faang.school.postservice.util.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@DirtiesContext
@Testcontainers
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class CommentListenerIntTest {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private KafkaProperty kafkaProperty;
    @Autowired
    private JsonMapper jsonMapper;
    @SpyBean
    private CommentListener listener;
    @MockBean
    private PostCacheService postCacheService;
    @Captor
    private ArgumentCaptor<String> dataCaptor;

    @Container
    public static PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:13.3");

    @DynamicPropertySource
    static void propertySource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
    }

    @Test
    @DisplayName("Успешное чтение CommentEvent для поста из Kafka")
    void positive_consumerCommentEventReceived() {
        CommentEvent event = createEvent();
        String expected = jsonMapper.toJson(event);

        kafkaTemplate.send(kafkaProperty.topic().commentNew(), expected);

        verify(listener, timeout(5000).times(1)).consumeNew(dataCaptor.capture(), any(Acknowledgment.class));
        String actual = dataCaptor.getValue();
        assertEquals(expected, actual);
    }

    private CommentEvent createEvent() {
        return CommentEvent.builder()
                .id(1)
                .build();
    }
}