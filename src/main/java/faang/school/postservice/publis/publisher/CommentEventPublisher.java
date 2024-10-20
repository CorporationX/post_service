package faang.school.postservice.publis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.comment.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(CommentEventDto message) {
        String valueAsString;
        String commentEventChannel = redisProperties.getCommentEventChannelName();
        try {
            valueAsString = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(commentEventChannel, valueAsString);
        log.info("Sending message to NotificationService: " + message);
    }
}
