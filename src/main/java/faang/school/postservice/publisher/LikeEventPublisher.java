package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic channelTopic;
    private final ObjectMapper objectMapper;

    public void publish(LikeEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(channelTopic.getTopic(), message);
            log.info("m: {}, t: {}", message, channelTopic.getTopic());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize LikeEvent", e);
            throw new RuntimeException(e);
        }
    }
}
