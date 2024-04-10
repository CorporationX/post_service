package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    abstract public void publish(T event);

    protected void send(T event, String topicChannelName) {
        try {
            String json = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topicChannelName, json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Json processing exception");
        }
    }
}
