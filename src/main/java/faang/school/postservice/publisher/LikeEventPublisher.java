package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redisEvent.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements EventPublisher<LikeEvent>{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventTopic;
    private final ObjectMapper objectMapper;


    @Override
    public void publish(LikeEvent event) {
        try {
            String message =  objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(likeEventTopic.getTopic(), message);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException(exception);
        }
    }
}
