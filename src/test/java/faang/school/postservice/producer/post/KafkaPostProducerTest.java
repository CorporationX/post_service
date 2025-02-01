package faang.school.postservice.producer.post;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KafkaPostProducerTest {
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private KafkaPostProducer kafkaPostProducer;

    @Test
    void testSuccessfulSendMessage() throws JsonProcessingException {
        PostEvent event = new PostEvent();
        String json = "test json";
        String topicName = "posts";

        ReflectionTestUtils.setField(kafkaPostProducer, "topicName", topicName);
        when(objectMapper.writeValueAsString(event)).thenReturn(json);

        kafkaPostProducer.sendMessage(event);

        verify(objectMapper).writeValueAsString(event);
        verify(kafkaTemplate).send(topicName, json);
    }

    @Test
    void testSendMessageThrowsException() throws JsonProcessingException {
        PostEvent event = new PostEvent();
        when(objectMapper.writeValueAsString(event)).thenThrow(mock(JsonProcessingException.class));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> kafkaPostProducer.sendMessage(event));
        assertEquals("Error converting object to json.", thrown.getMessage());
    }
}