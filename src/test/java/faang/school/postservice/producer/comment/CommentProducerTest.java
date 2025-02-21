package faang.school.postservice.producer.comment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.events.CommentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CommentProducer commentProducer;

    private final String topicName = "test-topic";

    @BeforeEach
    void setUp() {
        commentProducer = new CommentProducer(kafkaTemplate, objectMapper, topicName);
    }

    @Test
    void testSendMessage_Success() throws JsonProcessingException {
        CommentEvent commentEvent = new CommentEvent(1L, 2L, 3L);
        String jsonMessage = "{\"id\":1,\"postId\":2,\"authorId\":3}";

        when(objectMapper.writeValueAsString(commentEvent)).thenReturn(jsonMessage);

        commentProducer.sendMessage(commentEvent);

        verify(kafkaTemplate, times(1)).send(topicName, jsonMessage);
    }

    @Test
    void testSendMessage_JsonProcessingException() throws JsonProcessingException {
        CommentEvent commentEvent = new CommentEvent(1L, 2L, 3L);

        when(objectMapper.writeValueAsString(commentEvent)).thenThrow(new JsonProcessingException("Error") {});

        RuntimeException exception =
                org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                        () -> commentProducer.sendMessage(commentEvent));

        verify(kafkaTemplate, never()).send(anyString(), anyString());
        org.junit.jupiter.api.Assertions.assertEquals("Error converting object to json.", exception.getMessage());
    }
}

