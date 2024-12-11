package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.PostPublishedKafkaEvent;
import faang.school.postservice.service.FeedService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedHeatKafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FeedService feedService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private FeedHeatKafkaConsumer consumer;

    @Test
    void testProcessEvent_Success() {
        // Arrange
        PostPublishedKafkaEvent event = new PostPublishedKafkaEvent(1L, List.of(2L, 3L), LocalDateTime.now());

        // Act
        consumer.processEvent(event);

        // Assert
        verify(feedService, times(1)).addPost(event);
    }

    @Test
    void testConsume_Success() throws JsonProcessingException {
        // Arrange
        String jsonEvent = """
                {
                    "postId": 1,
                    "followerIds": [2, 3],
                    "publishedAt": "2024-12-10T15:30:00"
                }
                """;

        PostPublishedKafkaEvent event = new PostPublishedKafkaEvent(
                1L, List.of(2L, 3L), LocalDateTime.parse("2024-12-10T15:30:00"));
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", jsonEvent);

        when(objectMapper.readValue(jsonEvent, PostPublishedKafkaEvent.class)).thenReturn(event);

        // Act
        consumer.consume(record, acknowledgment);

        // Assert
        verify(feedService, times(1)).addPost(event);
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testConsume_ErrorHandling() throws JsonProcessingException {
        // Arrange
        String invalidJsonEvent = "{invalid json}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", invalidJsonEvent);

        when(objectMapper.readValue(invalidJsonEvent, PostPublishedKafkaEvent.class)).thenThrow(JsonProcessingException.class);

        // Act & Assert
        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                    consumer.consume(record, acknowledgment);
                });

        verify(feedService, never()).addPost(any());
        verify(acknowledgment, never()).acknowledge();
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("Failed to deserialize post"));
    }
}
