package faang.school.postservice.publisher.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private KafkaEventPublisher kafkaEventPublisher;

    @Test
    public void testSendEventWhenMessageSentSuccessfullyThenNoExceptionThrown() {
        String topic = "testTopic";
        String event = "testEvent";
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send(anyString(), any())).thenReturn(future);

        kafkaEventPublisher.sendEvent(topic, event);

        verify(kafkaTemplate, times(1)).send(topic, event);
    }

    @Test
    public void testSendEventWhenExceptionDuringMessageSendThenErrorMessageLogged() {
        String topic = "testTopic";
        String event = "testEvent";
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Test exception"));
        when(kafkaTemplate.send(anyString(), any())).thenReturn(future);

        kafkaEventPublisher.sendEvent(topic, event);

        verify(kafkaTemplate, times(1)).send(topic, event);
    }
}