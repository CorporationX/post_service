package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostCreatedEventDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostCreatedKafkaPublisherTest {

    @Mock
    private PostCreatedKafkaProducer producer;

    @InjectMocks
    private PostCreatedKafkaPublisher publisher;

    @Test
    @DisplayName("on should delegate PostCreatedEvent to Kafka producer")
    void on_shouldDelegateToProducer() {
        PostCreatedEventDto event = buildEvent();

        publisher.on(event);

        verify(producer).send(event);
        verifyNoMoreInteractions(producer);
    }

    @Test
    @DisplayName("on should propagate exception from Kafka producer")
    void on_shouldPropagateException() {

        PostCreatedEventDto event = buildEvent();
        doThrow(new RuntimeException("Kafka error"))
                .when(producer).send(event);

        assertThrows(RuntimeException.class, () -> publisher.on(event));

        verify(producer).send(event);
    }

    private static PostCreatedEventDto buildEvent() {
        return new PostCreatedEventDto(
                UUID.randomUUID(),
                Instant.parse("2025-12-28T00:00:00Z"),
                999L,
                null,
                null,
                1
        );
    }
}
