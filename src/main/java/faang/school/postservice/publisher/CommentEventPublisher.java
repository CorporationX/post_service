package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String commentTopic;

    public void publish(CommentEventDto commentEvent) {
        log.info("Start sending notification to REDIS");
        try {
            String json = objectMapper.writeValueAsString(commentEvent);
            redisTemplate.convertAndSend(commentTopic, json);
            log.info("End of sending notification to REDIS: " + json);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException", e);
            throw new RuntimeException(e);
        }
    }
}
