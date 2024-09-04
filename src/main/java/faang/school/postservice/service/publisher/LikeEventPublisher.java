package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.publishable.LikeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent>{
    public LikeEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.data.redis.channels.like}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
