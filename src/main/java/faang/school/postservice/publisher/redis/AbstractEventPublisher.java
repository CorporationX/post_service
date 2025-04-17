package faang.school.postservice.publisher.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic eventTopic;

    public void publish(T eventType){
        try {
            String json = objectMapper.writeValueAsString(eventType);
            redisTemplate.convertAndSend(eventTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
