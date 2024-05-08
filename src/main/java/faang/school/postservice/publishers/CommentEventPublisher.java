package faang.school.postservice.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import faang.school.postservice.dto.comment.CommentEvent;

@Component
public class CommentEventPublisher extends AbstractPublisher<CommentEvent> {
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              String channelName) {
        super(redisTemplate, objectMapper, channelName);
    }
}
