package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractEventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    protected void publish(T event, ChannelTopic topic) {
        try {
            String eventJson = objectMapper.writeValueAsString(event);
            log.info("Serialized event for JSON: {}", eventJson);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialized event for JSON.", e);
            throw new RuntimeException("Error serializing event to JSON: " + e.getMessage(), e);
        }
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
