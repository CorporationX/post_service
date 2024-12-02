package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.PostEventKafka;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaPostProducerTest {
    @Mock
    private KafkaTemplate<String, Object> multiTypeKafkaTemplate;

    private KafkaPostProducer kafkaPostProducer;
    private final List<Long> followerIds = List.of(1L, 2L, 3L);
    private PostEventKafka event;
    private final String topic = "post_topic";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaPostProducer = new KafkaPostProducer(multiTypeKafkaTemplate, topic);
        event = PostEventKafka.builder()
                .postId(1L).authorId(2L)
                .createdAt(LocalDateTime.now())
                .followerIds(followerIds).build();
    }

    @Test
    public void testSendSuccess() {
        kafkaPostProducer.sendEvent(event);

        verify(multiTypeKafkaTemplate, times(1)).send(topic, event);
    }

    @Test
    void testSendEventThrowsKafkaException() {
        doThrow(new KafkaException("Kafka error")).when(multiTypeKafkaTemplate).send(topic, event);

        KafkaException thrown = assertThrows(KafkaException.class, () -> kafkaPostProducer.sendEvent(event));

        verify(multiTypeKafkaTemplate, times(1)).send(topic, event);
        assertEquals("An unexpected error occurred while publishing event to kafka: " + event, thrown.getMessage());
    }
}