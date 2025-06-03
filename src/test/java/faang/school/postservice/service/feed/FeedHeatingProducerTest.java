package faang.school.postservice.service.feed;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FeedHeatingProducerTest {
    private static final String TOPIC = "feed-heating-topic";
    private static final Long USER_ID = 123L;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    private FeedHeatingProducer feedHeatingProducer;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenReturn("mock-json");
        feedHeatingProducer = new FeedHeatingProducer(kafkaTemplate, objectMapper, TOPIC);
    }

    @Test
    void testSendHeatingTask_Success() {
        feedHeatingProducer.sendHeatingTask(USER_ID);
        verify(kafkaTemplate, times(1)).send(eq(TOPIC), eq("mock-json"));
    }

    @Test
    void testSendHeatingTask_WhenObjectMapperThrows_ShouldThrowRuntimeException() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException("serialize error") {
        });

        RuntimeException ex = assertThrows(RuntimeException.class, () -> feedHeatingProducer.sendHeatingTask(USER_ID),
                "Expected a RuntimeException to be thrown when JsonProcessingException occurs");

        assertTrue(ex.getMessage().contains("serialize error"));
    }

    @Test
    void testSendHeatingTask_KafkaTemplateThrowsRuntimeException_ShouldPropagate() {
        doThrow(new RuntimeException("kafka down")).when(kafkaTemplate).send(eq(TOPIC), anyString());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> feedHeatingProducer.sendHeatingTask(USER_ID),
                "Expected a RuntimeException to be propagated when KafkaTemplate.send fails");

        assertEquals("kafka down", ex.getMessage());
    }
}
