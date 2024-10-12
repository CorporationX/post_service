package faang.school.postservice.service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikePostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikePostEventPublisher implements MessagePublisher {

    private final ObjectMapper objectMapper;
    private final RedisTemplate redisTemplate;
    private final ChannelTopic likePostTopic;

    public void publishEvent(LikePostEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            publish(message);
        } catch (JsonProcessingException ex) {
            log.error("Failed to created message with event - {}. {}", event.toString(), ex.getMessage());
        }
    }

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(likePostTopic.getTopic(), message);
        log.info("Message was send {}, in topic - {}", message, likePostTopic.getTopic());
    }
}
