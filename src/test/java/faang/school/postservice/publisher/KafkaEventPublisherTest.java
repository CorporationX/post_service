package faang.school.postservice.publisher;

import faang.school.postservice.model.event.EventType;
import faang.school.postservice.properties.KafkaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private KafkaProperties kafkaProperties;

    @InjectMocks
    private KafkaEventPublisher publisher;

    private final EventType eventType = EventType.LIKED_POST;
    private final String topicName = "notification.post.liked";

    @Test
    void publish_shouldSendToCorrectTopic() {
        when(kafkaProperties.getTopic(eventType)).thenReturn(topicName);

        Object object = new Object();

        publisher.publish(eventType, object);

        verify(kafkaTemplate, times(1)).send(topicName, object);
    }

    @Test
    void publish_whenTopicNotFound_shouldNotSend() {
        when(kafkaProperties.getTopic(EventType.COMMENT_CREATED)).thenReturn(null);

        publisher.publish(EventType.COMMENT_CREATED, new Object());

        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}