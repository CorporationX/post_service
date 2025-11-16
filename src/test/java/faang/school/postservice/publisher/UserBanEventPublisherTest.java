package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.UserBanEvent;
import faang.school.postservice.exception.EventPublishingException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserBanEventPublisherTest {

    private final String userBanTopic = "user-ban-topic";
    private final UserBanEvent event = new UserBanEvent(List.of(1L, 2L));
    private final String json = "{\"userIds\":[1,2]}";

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserBanEventPublisher publisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(publisher, "userBanTopic", userBanTopic);
    }

    @Test
    void publish_ShouldSendEventToKafka() throws Exception {
        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(mock(SendResult.class)));

        publisher.publish(event);

        verify(kafkaTemplate).send(userBanTopic, json);
        verify(objectMapper).writeValueAsString(event);
    }

    @Test
    void publish_ShouldLogError_WhenKafkaFails() throws Exception {
        when(objectMapper.writeValueAsString(event)).thenReturn(json);
        when(kafkaTemplate.send(anyString(), anyString()))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Kafka error")));

        publisher.publish(event);

        verify(kafkaTemplate).send(userBanTopic, json);
    }

    @Test
    void publish_ShouldThrowEventPublishingException_WhenJsonFails() throws Exception {
        when(objectMapper.writeValueAsString(event)).thenThrow(JsonProcessingException.class);

        Assertions.assertThrows(EventPublishingException.class, () -> publisher.publish(event));
        verify(kafkaTemplate, Mockito.never()).send(anyString(), anyString());
    }
}