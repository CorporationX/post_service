package faang.school.postservice.kafka.producer;

import faang.school.postservice.kafka.event.NewLikeEvent;
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
class KafkaLikeProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaLikeProducer kafkaLikeProducer;

    private NewLikeEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = NewLikeEvent.builder()
                .postId(1L)
                .userId(1L)
                .build();
    }

    @Test
    void sendLikeEvent_WhenSuccessful_ShouldSendMessage() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), eq(testEvent)))
                .thenReturn(future);

        kafkaLikeProducer.sendLikeEvent(testEvent);

        verify(kafkaTemplate).send("new-likes", testEvent);
    }

    @Test
    void sendLikeEvent_WhenKafkaFails_ShouldThrowException() {
        RuntimeException testException = new RuntimeException("Kafka error");
        when(kafkaTemplate.send(anyString(), eq(testEvent)))
                .thenThrow(testException);

        assertThrows(RuntimeException.class, () ->
                kafkaLikeProducer.sendLikeEvent(testEvent)
        );
        verify(kafkaTemplate).send("new-likes", testEvent);
    }

    @Test
    void sendLikeEvent_WhenEventIsNull_ShouldThrowNullPointerException() {
        NewLikeEvent nullEvent = null;
        NullPointerException exception = assertThrows(NullPointerException.class, () ->
                kafkaLikeProducer.sendLikeEvent(nullEvent)
        );

        assertEquals("Cannot invoke \"faang.school.postservice.kafka.event.NewLikeEvent.getPostId()\" because \"event\" is null", exception.getMessage());
    }

    @Test
    void sendLikeEvent_ShouldUseCorrectTopic() {
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), eq(testEvent)))
                .thenReturn(future);

        kafkaLikeProducer.sendLikeEvent(testEvent);

        verify(kafkaTemplate).send(eq("new-likes"), any());
    }
}