package faang.school.postservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.service.publisher.KafkaPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static faang.school.postservice.service.publisher.KafkaPublisher.FAILED_SERIALIZING_OBJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class KafkaPublisherTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @InjectMocks
    private KafkaPublisher kafkaPublisher;

    private final String testTopic = "test-topic";
    private final TestObject testObject = new TestObject("test", 123);

    private static class TestObject {
        String field1;
        int field2;

        public TestObject(String field1, int field2) {
            this.field1 = field1;
            this.field2 = field2;
        }
    }

    @Test
    void shouldSendSerializedMessageToKafka() throws JsonProcessingException {
        String serializedObject = "{\"field1\":\"test\",\"field2\":123}";
        when(objectMapper.writeValueAsString(testObject)).thenReturn(serializedObject);

        kafkaPublisher.send(testTopic, testObject);

        verify(objectMapper).writeValueAsString(testObject);
        verify(kafkaTemplate).send(eq(testTopic), eq(serializedObject));
    }

    @Test
    void shouldThrowJsonProcessingException() throws JsonProcessingException {
        JsonProcessingException jsonException = new JsonProcessingException("Error") {};
        when(objectMapper.writeValueAsString(any())).thenThrow(jsonException);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> kafkaPublisher.send(testTopic, testObject));

        assertEquals(FAILED_SERIALIZING_OBJECT, exception.getMessage());
    }
}
