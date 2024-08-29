package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> implements MessagePublisher<T> {

    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(T event) {
        String message;
        try {
            message =  objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException e) {
            log.error("Error while creating event message", e);
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(getChannelTopic(), message);
        log.info("Published event {}", event);
    }

    protected abstract String getChannelTopic();
}
