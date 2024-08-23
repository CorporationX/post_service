package faang.school.postservice.publishers_like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.events.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher implements MessagePublisher<LikeEvent> {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likePost;

    @Override
    public void publish(LikeEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(likePost.getTopic(), message);
            log.info("Published recommendation: {}", event);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize recommendation: {}", event, e);
        }
    }
}