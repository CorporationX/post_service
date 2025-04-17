package faang.school.postservice.publisher.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final ChannelTopic postEventTopic;

    public void publish(PostEvent postEvent) {
        try {
            String  json = objectMapper.writeValueAsString(postEvent);
            redisTemplate.convertAndSend(postEventTopic.getTopic(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
