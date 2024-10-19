package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public abstract class AbstractEventPublisher<T, K> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserContext userContext;
    private final ObjectMapper objectMapper;

    public void publish(T event) {
        try {
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(getTopicName(), jsonEvent);
            log.info("Event {} was published to topic {}", event, getTopicName());
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Failed to convert event to json");
        } catch (Exception exception) {
            log.error("Failed to send event to Redis", exception);
        }
    }

    protected abstract T convert(K entity);

    protected abstract String getTopicName();

    protected long actorId() {
        return userContext.getUserId();
    }
}
