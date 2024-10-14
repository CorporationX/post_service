package faang.school.postservice.service.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;

public abstract class AbstractEventPublisher<T> {

    protected final RedisTemplate<String, Object> redisTemplate;
    protected final ObjectMapper objectMapper;

    protected AbstractEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    protected String eventJson(T event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while converting " + event + " to JSON", e);
        }
    }
}
