package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            log.info("A message has been sent to channel {}, message: {}", channel, messageJson);
        } catch (JsonProcessingException e) {
            log.error("An exception was thrown when reading a message in RedisMessagePublisher: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
