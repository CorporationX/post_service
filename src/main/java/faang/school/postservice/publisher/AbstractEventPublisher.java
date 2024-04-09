package faang.school.postservice.publisher;

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
    private final ChannelTopic topic;
    private final ObjectMapper mapper;

    public void publish(T event) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), mapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Can't converting object to string");
        }
    }
}