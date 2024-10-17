package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements EventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(T event, Topic topic) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(topic.getTopic(), jsonEvent);
            log.info("Event published to Redis topic {}: {}", topic.getTopic(), jsonEvent);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert event to JSON", e);
            throw new RuntimeException("Failed to convert event to JSON", e);
        } catch (Exception e) {
            log.error("Failed to publish event to Redis", e);
            throw new RuntimeException("Failed to publish event to Redis", e);
        }
    }
}