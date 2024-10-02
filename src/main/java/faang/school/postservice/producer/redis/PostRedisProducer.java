package faang.school.postservice.producer.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostRedisProducer extends AbstractRedisProducer<PostEvent> {
    public PostRedisProducer(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.data.redis.channels.post}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
