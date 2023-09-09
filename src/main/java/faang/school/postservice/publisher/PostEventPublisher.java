package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.PostEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractPublisher<PostEventDto> {
    public PostEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper jsonMapper,
                              @Value("${spring.data.redis.channels.post_channels.name}") String topic) {
        super(redisTemplate, jsonMapper, topic);
    }
}
