package faang.school.postservice.publisher;

import faang.school.postservice.event.UserBanEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBanEventPublisherTest {

    private final String userBanTopic = "user-ban-topic";

    @Mock
    private KafkaTemplate<String, UserBanEvent> kafkaTemplate;

    @InjectMocks
    private UserBanEventPublisher publisher;

    private UserBanEvent event;

    @BeforeEach
    void setUp() {
        // Подставляем значение из application.yml, чтоб не тянуть контекст
        ReflectionTestUtils.setField(publisher, "userBanTopic", userBanTopic);
        event = new UserBanEvent(List.of(1L, 2L));
    }

    @Test
    void publish_ShouldSendEventToKafka() {
        CompletableFuture<SendResult<String, UserBanEvent>> future = new CompletableFuture<>();
        future.complete(mock(SendResult.class));

        when(kafkaTemplate.send(eq(userBanTopic), any(UserBanEvent.class)))
                .thenReturn(future);

        assertDoesNotThrow(() -> publisher.publish(event));

        verify(kafkaTemplate).send(userBanTopic, event);
    }

    @Test
    void publish_ShouldNotThrow_WhenKafkaFutureCompletesExceptionally() {
        CompletableFuture<SendResult<String, UserBanEvent>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka error"));

        when(kafkaTemplate.send(eq(userBanTopic), any(UserBanEvent.class)))
                .thenReturn(future);

        // publish сам ничего не кидает — просто логирует
        assertDoesNotThrow(() -> publisher.publish(event));

        verify(kafkaTemplate).send(userBanTopic, event);
    }

    @Test
    void publish_ShouldPropagateException_WhenKafkaSendThrowsSynchronously() {
        when(kafkaTemplate.send(eq(userBanTopic), any(UserBanEvent.class)))
                .thenThrow(new RuntimeException("Kafka send failed"));

        assertThrows(RuntimeException.class, () -> publisher.publish(event));

        verify(kafkaTemplate).send(eq(userBanTopic), any(UserBanEvent.class));
    }
}
