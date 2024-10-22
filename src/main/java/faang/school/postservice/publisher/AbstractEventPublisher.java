package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@Component
public abstract class AbstractEventPublisher<T, K> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserContext userContext;
    private final ObjectMapper objectMapper;

    public void sendEntityToAnalytics(K entity, String viewChannel) {
        try {
            log.info("Entering publishPostEvent advice. Return value: {}", entity);
            long actorId = userContext.getUserId();
            log.info("Actor id = {}", actorId);
            T event = createEvent(entity, actorId);
            String jsonEvent = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(viewChannel, jsonEvent);
            log.info("Message published {} to topic {}", event, viewChannel);
        } catch (JsonProcessingException jsonProcessingException) {
            log.error("Failed to convert event to json");
        } catch (Exception exception) {
            log.error("Failed to send event to Redis", exception);
        }
    }

    public abstract T createEvent(K entity, Long actorId);
}

