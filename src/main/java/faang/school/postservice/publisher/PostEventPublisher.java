package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventPublisher implements EventPublisher<PostEvent> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postEventTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PostEvent event) {
        try {
            String message =  objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(postEventTopic.getTopic(), message);
        } catch (JsonProcessingException exception){
            throw new RuntimeException(exception);
        }
    }
}
