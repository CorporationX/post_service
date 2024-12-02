package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.dto.PostDto;
import faang.school.postservice.model.event.kafka.PostViewEventKafka;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KafkaPostViewProducerTest {
    private static final String TOPIC = "test-post-view-topic";
    private PostViewEventKafka event;

    @Mock
    private KafkaTemplate<String, Object> multiTypeKafkaTemplate;

    @InjectMocks
    private KafkaPostViewProducer kafkaPostViewProducer;

    @BeforeEach
    void setUp() {
        kafkaPostViewProducer = new KafkaPostViewProducer(multiTypeKafkaTemplate, TOPIC);

        PostDto postDto = PostDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(123L)
                .build();

        event = new PostViewEventKafka(postDto);
    }

    @Test
    @DisplayName("Should send event successfully")
    void testSend_Success() {
        kafkaPostViewProducer.sendEvent(event);

        verify(multiTypeKafkaTemplate, times(1)).send(TOPIC, event);
    }

    @Test
    @DisplayName("Should throw KafkaException when sending fails")
    void testSend_Fail() {
        doThrow(new KafkaException("Kafka error")).when(multiTypeKafkaTemplate).send(TOPIC, event);

        KafkaException exception = assertThrows(KafkaException.class, () -> kafkaPostViewProducer.sendEvent(event));

        assert exception.getMessage().contains("An unexpected error occurred while publishing event to kafka: " + event);

        verify(multiTypeKafkaTemplate, times(1)).send(TOPIC, event);
    }
}
