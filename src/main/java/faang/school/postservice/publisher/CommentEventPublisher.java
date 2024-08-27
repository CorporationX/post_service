package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 @Value("${spring.data.redis.channels.comment_channel.name}") String channelName) {
        super(redisTemplate, objectMapper, channelName);
    }

}
