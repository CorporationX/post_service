package faang.school.postservice.publisher.comment;

import faang.school.postservice.config.redis.RedisTopicsFactory;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.publisher.AbstractEventsPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component
public class RedisCommentEventPublisher extends AbstractEventsPublisher<CommentEvent> {
    private final Topic commentTopic;

    public RedisCommentEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            RedisTopicsFactory redisTopicsFactory,
            @Value("${spring.data.redis.channels.comment_event_channel.name}") String commentEventChannel) {
        super(redisTemplate);
        this.commentTopic = redisTopicsFactory.getTopic(commentEventChannel);
    }

    public void publishCommentEvent(CommentEvent event) {
        publish(event, commentTopic);
    }
}
