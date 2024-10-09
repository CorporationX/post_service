package faang.school.postservice.publisher;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class LikeEventPublisher {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventTopic;

    public void publishLikeEvent(LikeEvent event) {
        String jsonEvent = writeEvent(event);
        redisTemplate.convertAndSend(likeEventTopic.getTopic(), jsonEvent);
    }

    private String writeEvent(LikeEvent event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
