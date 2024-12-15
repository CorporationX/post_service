package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    @Value("${spring.data.redis.channels.comment-channel.name}")
    private String commentEventTopic;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(CommentEvent commentEvent) {
        try {
            String json = objectMapper.writeValueAsString(commentEvent);
            log.info("Publishing comment event: {}", json);
            redisTemplate.convertAndSend(commentEventTopic, json);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize comment event: {}", commentEvent, e);
            throw new RuntimeException(e);
        }
    }
}
