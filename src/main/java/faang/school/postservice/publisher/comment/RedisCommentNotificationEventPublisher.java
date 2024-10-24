package faang.school.postservice.publisher.comment;

import faang.school.postservice.config.redis.RedisTopicsFactory;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.dto.comment.CommentNotificationEvent;
import faang.school.postservice.publisher.AbstractEventsPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component
public class RedisCommentNotificationEventPublisher extends AbstractEventsPublisher<CommentNotificationEvent> {
    private final Topic commentNotificationTopic;

    public RedisCommentNotificationEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            RedisTopicsFactory redisTopicsFactory,
            @Value("${spring.data.redis.channels.comment_notification_event_channel.name}") String commentNotificationEventChannel) {
        super(redisTemplate);
        this.commentNotificationTopic = redisTopicsFactory.getTopic(commentNotificationEventChannel);
    }

    public void publishCommentNotificationEvent(CommentNotificationEvent event) {
        publish(event, commentNotificationTopic);
    }
}
