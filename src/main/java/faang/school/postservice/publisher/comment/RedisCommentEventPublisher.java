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
    @Value("${spring.data.redis.channels.comment_event_channel.name}")
    private String commentEventChannel;
    private final RedisTopicsFactory redisTopicsFactory;


    public RedisCommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                      ObjectMapper objectMapper,
                                      RedisTopicsFactory redisTopicsFactory) {
        super(redisTemplate, objectMapper);
        this.redisTopicsFactory = redisTopicsFactory;
    }

    public void publishCommentEvent(CommentEvent event) {
        Topic commentTopic = redisTopicsFactory.getTopic(commentEventChannel);
        publish(event, commentTopic);
    }
}
