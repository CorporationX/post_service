package faang.school.postservice.kafka.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import faang.school.postservice.service.RedisPostService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeKafkaConsumerTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RedisPostService redisPostService;

    @Mock
    private Acknowledgment acknowledgment;

    @InjectMocks
    private LikeKafkaConsumer consumer;

    @Test
    void testProcessEvent_Success() {
        // Arrange
        LikeKafkaEvent event = new LikeKafkaEvent(1L, 2L);

        // Act
        consumer.processEvent(event);

        // Assert
        verify(redisPostService, times(1)).incrementLikesWithTransaction(event.getPostId(), event.getLikeId());
    }

    @Test
    void testOnMessage_Success() throws JsonProcessingException {
        // Arrange
        String jsonEvent = """
                {
                    "postId": 1,
                    "likeId": 2
                }
                """;

        LikeKafkaEvent event = new LikeKafkaEvent(1L, 2L);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", jsonEvent);

        when(objectMapper.readValue(jsonEvent, LikeKafkaEvent.class)).thenReturn(event);

        // Act
        consumer.onMessage(record, acknowledgment);

        // Assert
        verify(redisPostService, times(1)).incrementLikesWithTransaction(event.getPostId(), event.getLikeId());
        verify(acknowledgment, times(1)).acknowledge();
    }

    @Test
    void testOnMessage_ErrorHandling() throws JsonProcessingException {
        // Arrange
        String invalidJsonEvent = "{invalid json}";
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L, "key", invalidJsonEvent);

        when(objectMapper.readValue(invalidJsonEvent, LikeKafkaEvent.class)).thenThrow(JsonProcessingException.class);

        // Act & Assert
        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () -> {
                    consumer.onMessage(record, acknowledgment);
                });

        verify(redisPostService, never()).incrementLikesWithTransaction(anyLong(), anyLong());
        verify(acknowledgment, never()).acknowledge();
        org.junit.jupiter.api.Assertions.assertTrue(exception.getMessage().contains("Failed to deserialize like event"));
    }
}
