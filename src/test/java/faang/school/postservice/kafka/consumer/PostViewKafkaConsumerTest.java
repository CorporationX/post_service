package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.PostViewKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostViewKafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisPostService redisPostService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private PostViewKafkaConsumer consumer;

    @Test
    void testProcessEvent_Success() {
        // Arrange
        PostViewKafkaEvent event = new PostViewKafkaEvent(1L, 2L, "2024-12-10T15:30:00");

        // Act
        consumer.processEvent(event);

        // Assert
        verify(redisPostService, times(1)).incrementPostViewsWithTransaction(
                event.getPostId(),
                event.getViewerId(),
                event.getViewDateTime());
    }

    @Test
    void testOnMessage_Success() throws JsonProcessingException {
        // Arrange
        String jsonEvent = """
                {
                    "postId": 1,
                    "viewerId": 2,
                    "viewDateTime": "2024-12-10T15:30:00"
                }
                """;

        PostViewKafkaEvent event = new PostViewKafkaEvent(1L, 2L, "2024-12-10T15:30:00");
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", jsonEvent);

        when(objectMapper.readValue(jsonEvent, PostViewKafkaEvent.class)).thenReturn(event);

        // Act
        consumer.onMessage(record, acknowledgment);

        // Assert
        verify(redisPostService, times(1)).incrementPostViewsWithTransaction(
                event.getPostId(),
                event.getViewerId(),
                event.getViewDateTime());
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testOnMessage_ErrorHandling() throws JsonProcessingException {
        // Arrange
        String invalidJsonEvent = "{invalid json}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", invalidJsonEvent);

        when(objectMapper.readValue(invalidJsonEvent, PostViewKafkaEvent.class)).thenThrow(JsonProcessingException.class);

        // Act & Assert
        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                    consumer.onMessage(record, acknowledgment);
                });

        verify(redisPostService, never()).incrementPostViewsWithTransaction(anyLong(), anyLong(), anyString());
        verify(acknowledgment, never()).acknowledge();
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("Failed to deserialize post view event"));
    }
}
