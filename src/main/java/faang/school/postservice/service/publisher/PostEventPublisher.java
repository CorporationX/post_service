package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.publishable.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventPublisher<PostEvent> {
    public PostEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.data.redis.channels.post}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
