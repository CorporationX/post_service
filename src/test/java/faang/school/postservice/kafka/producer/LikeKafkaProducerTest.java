package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.LikeKafkaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class LikeKafkaProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LikeKafkaProducer producer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Set the topic value for testing
        producer = new LikeKafkaProducer(kafkaTemplate, objectMapper) {
            @Override
            protected String getTopic() {
                return "like-topic";
            }
        };
    }

    @Test
    void testSendEvent_Success() throws JsonProcessingException {
        // Arrange
        LikeKafkaEvent event = new LikeKafkaEvent(1L, 2L);
        String eventJson = "{\"postId\":1,\"likeId\":2}";

        when(objectMapper.writeValueAsString(event)).thenReturn(eventJson);

        // Act
        producer.sendEvent(event);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertEquals("like-topic", topicCaptor.getValue());
        assertEquals(eventJson, messageCaptor.getValue());
    }

    @Test
    void testSendEvent_Failure() throws JsonProcessingException {
        // Arrange
        LikeKafkaEvent event = new LikeKafkaEvent(1L, 2L);

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> producer.sendEvent(event));
        assertEquals("Failed to serialize event to JSON", exception.getMessage());

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
