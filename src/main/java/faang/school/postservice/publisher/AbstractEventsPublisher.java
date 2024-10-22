package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.Topic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventsPublisher<T> implements EventPublisher<T> {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(T event, Topic topic) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), event);
            log.info("Event published to Redis topic {}: {}", topic.getTopic(), event);
        } catch (Exception e) {
            log.error("Failed to publish event to Redis", e);
            throw new RuntimeException("Failed to publish event to Redis", e);
        }
    }
}
