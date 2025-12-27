package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostCreatedEventDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCreatedKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, PostCreatedEventDto> kafkaTemplate;

    @InjectMocks
    private PostCreatedKafkaProducer producer;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                producer,
                "postCreatedEventTopic",
                "posts-created-topic"
        );
    }

    @Test
    @DisplayName("send should publish PostCreatedEvent to Kafka with postId as key")
    void send_shouldPublishEventWithCorrectKeyAndTopic() {

        PostCreatedEventDto event = buildEvent();
        CompletableFuture<SendResult<String, PostCreatedEventDto>> future =
                CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send(
                eq("posts-created-topic"),
                eq(String.valueOf(event.postId())),
                eq(event)
        )).thenReturn(future);

        producer.send(event);

        verify(kafkaTemplate).send(
                "posts-created-topic",
                String.valueOf(event.postId()),
                event
        );
    }

    @Test
    @DisplayName("send should handle Kafka send failure gracefully")
    void send_shouldHandleKafkaFailure() {

        PostCreatedEventDto event = buildEvent();
        CompletableFuture<SendResult<String, PostCreatedEventDto>> future =
                new CompletableFuture<>();

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(future);


        producer.send(event);

        // simulate async failure
        future.completeExceptionally(new RuntimeException("Kafka unavailable"));

        verify(kafkaTemplate).send(
                "posts-created-topic",
                String.valueOf(event.postId()),
                event
        );
    }

    @Test
    @DisplayName("send should be asynchronous and not block caller thread")
    void send_shouldBeAsync() {

        when(kafkaTemplate.send(anyString(), anyString(), any()))
                .thenReturn(new CompletableFuture<>());

        producer.send(buildEvent());

        verify(kafkaTemplate).send(anyString(), anyString(), any());
    }

    private static PostCreatedEventDto buildEvent() {
        return new PostCreatedEventDto(
                UUID.randomUUID(),
                Instant.parse("2025-12-28T00:00:00Z"),
                123L,
                null,
                null,
                1
        );
    }
}
