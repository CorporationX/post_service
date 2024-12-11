package faang.school.postservice.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.kafka.AuthorPostByHeatKafkaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthorPostByHeatKafkaProducerTest {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private AuthorPostByHeatKafkaProducer producer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // Set the topic value
        producer = new AuthorPostByHeatKafkaProducer(kafkaTemplate, objectMapper) {
            @Override
            protected String getTopic() {
                return "author-post-by-heat-topic";
            }
        };
    }

    @Test
    void testSendEvent_Success() throws JsonProcessingException {
        // Arrange
        AuthorPostByHeatKafkaEvent event = new AuthorPostByHeatKafkaEvent(123L, 456L, List.of(789L, 101L), LocalDateTime.parse("2024-12-10T15:30:00", formatter));
        String eventJson = "{\"postId\":123,\"authorId\":456,\"lastCommentAuthors\":[789,101],\"publishedAt\":\"2024-12-10T15:30:00\"}";

        when(objectMapper.writeValueAsString(event)).thenReturn(eventJson);

        // Act
        producer.sendEvent(event);

        // Assert
        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(topicCaptor.capture(), messageCaptor.capture());

        assertEquals("author-post-by-heat-topic", topicCaptor.getValue());
        assertEquals(eventJson, messageCaptor.getValue());
    }

    @Test
    void testSendEvent_Failure() throws JsonProcessingException {
        // Arrange
        AuthorPostByHeatKafkaEvent event = new AuthorPostByHeatKafkaEvent(123L, 456L, List.of(789L, 101L), LocalDateTime.parse("2024-12-10T15:30:00", formatter));

        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {
        });

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> producer.sendEvent(event));
        assertEquals("Failed to serialize event to JSON", exception.getMessage());

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }
}
