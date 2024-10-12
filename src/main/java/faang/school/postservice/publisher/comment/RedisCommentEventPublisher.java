package faang.school.postservice.publisher.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component
public class RedisCommentEventPublisher extends AbstractEventPublisher<CommentEvent> {
    public RedisCommentEventPublisher(RedisTemplate<String, Object> redisTemplate, Topic commentEvent,
                                    ObjectMapper objectMapper) {
        super(redisTemplate, commentEvent, objectMapper);
    }
}
