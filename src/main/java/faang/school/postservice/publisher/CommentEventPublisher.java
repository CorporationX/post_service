package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic, ObjectMapper mapper) {
        super(redisTemplate, topic, mapper);
    }
}