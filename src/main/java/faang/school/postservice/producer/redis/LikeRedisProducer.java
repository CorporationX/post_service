package faang.school.postservice.producer.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.LikePostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeRedisProducer extends AbstractRedisProducer<LikePostEvent> {
    public LikeRedisProducer(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            @Value("${spring.data.redis.channels.like}") String topic) {
        super(redisTemplate, objectMapper, topic);
    }
}
