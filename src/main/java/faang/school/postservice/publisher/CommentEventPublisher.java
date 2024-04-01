package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {

    @Value("${spring.data.redis.channels.comment}")
    private String commentChannel;

    public CommentEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    @Override
    public void publish(CommentEventDto event) {
        convertAndSend(event, commentChannel);
    }
}
