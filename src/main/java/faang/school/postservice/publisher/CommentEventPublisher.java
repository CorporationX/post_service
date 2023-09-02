package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.CommentEventDto;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractPublisher<CommentEventDto>{
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper jsonMapper,
                                 @Value("${spring.data.redis.channels.comment-event-channel.name}")String channel) {
        super(redisTemplate, jsonMapper, channel);
    }
}