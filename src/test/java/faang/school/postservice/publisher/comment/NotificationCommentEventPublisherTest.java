package faang.school.postservice.publisher.comment;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.event.NotificationCommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyNoInteractions;

@Slf4j
@ExtendWith(MockitoExtension.class)
class NotificationCommentEventPublisherTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private NotificationCommentEventPublisher notificationCommentEventPublisher;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                notificationCommentEventPublisher,
                "notificationCommentTopicName",
                "test-comment-notification-topic"
        );
    }

    @Test
    void publishEventSuccess() throws JsonProcessingException {
        Comment comment = new Comment();
        NotificationCommentEvent notificationEvent = new NotificationCommentEvent();
        String eventJson = "{\"some\":\"json\"}";

        when(commentMapper.toNotificationCommentEvent(comment)).thenReturn(notificationEvent);
        when(objectMapper.writeValueAsString(notificationEvent)).thenReturn(eventJson);

        notificationCommentEventPublisher.publishEvent(comment);

        verify(commentMapper).toNotificationCommentEvent(comment);
        verify(objectMapper).writeValueAsString(notificationEvent);
        verify(kafkaTemplate).send("test-comment-notification-topic", eventJson);

        verifyNoMoreInteractions(commentMapper, objectMapper, kafkaTemplate);
    }

    @Test
    void publishEventJsonException() throws JsonProcessingException {
        Comment comment = new Comment();
        NotificationCommentEvent notificationEvent = new NotificationCommentEvent();

        when(commentMapper.toNotificationCommentEvent(comment)).thenReturn(notificationEvent);
        when(objectMapper.writeValueAsString(notificationEvent))
                .thenThrow(new JsonProcessingException("Error writing JSON") {});

        assertThrows(RuntimeException.class,
                () -> notificationCommentEventPublisher.publishEvent(comment)
        );

        verify(commentMapper).toNotificationCommentEvent(comment);
        verify(objectMapper).writeValueAsString(notificationEvent);

        verifyNoInteractions(kafkaTemplate);
    }
}

