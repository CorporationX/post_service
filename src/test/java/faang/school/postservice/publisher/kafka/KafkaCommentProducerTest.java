package faang.school.postservice.publisher.kafka;

import faang.school.postservice.model.event.kafka.CommentEventKafka;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaCommentProducerTest {

    @Mock
    private KafkaTemplate<String, Object> multiTypeKafkaTemplate;

    private KafkaCommentProducer kafkaCommentProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaCommentProducer = new KafkaCommentProducer(multiTypeKafkaTemplate, "comment_topic");
    }

    @Test
    public void testSendSuccess() {
        CommentEventKafka event = new CommentEventKafka(1L, 2L, 3L,
                "someContent",LocalDateTime.now());

        kafkaCommentProducer.sendEvent(event);

        verify(multiTypeKafkaTemplate, times(1)).send("comment_topic", event);
    }

    @Test
    void testSendEventThrowsKafkaException() {
        CommentEventKafka event = new CommentEventKafka(1L, 2L, 3L,
                "someContent",LocalDateTime.now());
        doThrow(new KafkaException("Kafka error")).when(multiTypeKafkaTemplate).send("comment_topic", event);

        KafkaException thrown = assertThrows(KafkaException.class, () -> kafkaCommentProducer.sendEvent(event));

        verify(multiTypeKafkaTemplate, times(1)).send("comment_topic", event);
        assertEquals("An unexpected error occurred while publishing event to kafka: "+event, thrown.getMessage());
    }
}