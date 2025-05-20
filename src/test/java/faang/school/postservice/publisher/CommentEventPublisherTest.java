package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.properties.KafkaProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private KafkaProperties kafkaProperties;

    @Mock
    private KafkaProperties.Topic topicProperties;

    @InjectMocks
    private CommentEventPublisher publisher;

    private final String topic = "notification.comment.created";

    private final CommentEventDto event = CommentEventDto.builder()
            .commentId(1L)
            .commenterId(2L)
            .postId(3L)
            .postAuthorId(4L)
            .text("Test comment")
            .build();

    @BeforeEach
    void setUp() {
        when(kafkaProperties.getTopic()).thenReturn(topicProperties);
        when(topicProperties.getCommentCreatedNotification()).thenReturn(topic);
    }

    @Test
    void shouldPublishEventToKafka() {
        publisher.publish(event);

        verify(kafkaTemplate, times(1)).send(topic, event);
    }
}