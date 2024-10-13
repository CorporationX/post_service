package faang.school.postservice.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisherImpl implements LikeEventPublisher {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventTopic;

    public void publishLikeEvent(LikeEvent event) {
        String jsonEvent = writeEvent(event);
        redisTemplate.convertAndSend(likeEventTopic.getTopic(), jsonEvent);
    }

    private String writeEvent(LikeEvent event) {
        try {
            log.info("Returning event: {}", event);
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            log.error("file was not downloaded properly: {}", exception.getMessage());
            throw new RuntimeException(exception);
        }
    }
}
