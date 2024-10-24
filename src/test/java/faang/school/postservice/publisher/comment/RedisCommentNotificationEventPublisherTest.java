package faang.school.postservice.publisher.comment;

import faang.school.postservice.config.redis.RedisTopicsFactory;
import faang.school.postservice.dto.comment.CommentNotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RedisCommentNotificationEventPublisherTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private Topic commentNotificationTopic;

    private RedisCommentNotificationEventPublisher publisher;

    @BeforeEach
    void setUp() {
        RedisTopicsFactory redisTopicsFactory = mock(RedisTopicsFactory.class);
        when(redisTopicsFactory.getTopic("comment_notification_event_channel"))
                .thenReturn(commentNotificationTopic);
        when(commentNotificationTopic.getTopic()).thenReturn("comment_notification_event_channel");
        publisher = new RedisCommentNotificationEventPublisher(
                redisTemplate,
                redisTopicsFactory,
                "comment_notification_event_channel");
    }

    @Test
    void shouldPublishCommentNotificationEvent() {
        CommentNotificationEvent event = new CommentNotificationEvent(
                1L, 2L, 3L, 4L, "Test comment");
        publisher.publishCommentNotificationEvent(event);
        verify(redisTemplate).convertAndSend(eq("comment_notification_event_channel"), eq(event));
    }
}