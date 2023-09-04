package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.CommentEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractPublisher<CommentEventDto> {
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                 @Value("${spring.data.redis.channels.comment_channels.name}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
