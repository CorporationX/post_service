package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.NewCommentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaCommentProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaCommentProducer kafkaCommentProducer;

    private NewCommentEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = NewCommentEvent.builder()
                .postId(1L)
                .authorId(1L)
                .content("Test comment")
                .build();
    }

    @Test
    void sendCommentEvent_WhenSuccessful_ShouldSendMessage() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), eq(testEvent)))
                .thenReturn(future);

        kafkaCommentProducer.sendCommentEvent(testEvent);

        verify(kafkaTemplate).send("new-comments", testEvent);
    }

    @Test
    void sendCommentEvent_WhenKafkaFails_ShouldThrowException() {
        RuntimeException testException = new RuntimeException("Kafka error");
        when(kafkaTemplate.send(anyString(), eq(testEvent)))
                .thenThrow(testException);

        assertThrows(RuntimeException.class, () ->
                kafkaCommentProducer.sendCommentEvent(testEvent)
        );
        verify(kafkaTemplate).send("new-comments", testEvent);
    }

    @Test
    void sendCommentEvent_WhenEventIsNull_ShouldThrowNullPointerException() {
        NewCommentEvent nullEvent = null;
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                kafkaCommentProducer.sendCommentEvent(nullEvent)
        );

        assertEquals("Cannot invoke \"faang.school.postservice.kafka.event.NewCommentEvent.getPostId()\" because \"event\" is null", exception.getMessage());
    }

    @Test
    void sendCommentEvent_ShouldUseCorrectTopic() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), eq(testEvent)))
                .thenReturn(future);

        kafkaCommentProducer.sendCommentEvent(testEvent);

        verify(kafkaTemplate).send(eq("new-comments"), any());
    }
}