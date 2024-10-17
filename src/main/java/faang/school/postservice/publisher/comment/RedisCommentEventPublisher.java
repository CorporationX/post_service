package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisTopicsFactory;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component
public class RedisCommentEventPublisher extends AbstractEventPublisher<CommentEvent> {
    private final Topic commentTopic;

    public RedisCommentEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            RedisTopicsFactory redisTopicsFactory,
            @Value("${spring.data.redis.channels.comment_event_channel.name}") String commentEventChannel) {
        super(redisTemplate, objectMapper);
        this.commentTopic = redisTopicsFactory.getTopic(commentEventChannel);
    }

    public void publishCommentEvent(CommentEvent event) {
        publish(event, commentTopic);
    }
}
