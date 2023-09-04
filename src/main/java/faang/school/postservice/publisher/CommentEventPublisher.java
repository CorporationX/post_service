package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractPublisher<CommentEventDto> {

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
