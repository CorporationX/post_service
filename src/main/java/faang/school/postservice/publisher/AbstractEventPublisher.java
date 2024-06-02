package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper mapper;

    protected void publish(T event, String topic) {
        try {
            String json = mapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
