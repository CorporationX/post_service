package faang.school.postservice.publis.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.like.AbstractLikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final ObjectMapper objectMapper;
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;

    public void publishPostLikeEventToBroker(AbstractLikeEvent message) {
        String postLikeEventChannel = redisProperties.getPostLikeEventChannelName();
        publish(message, postLikeEventChannel);
    }

    public void publish(Object message, String likeEventChannel) {
        String valueAsString;
        try {
            valueAsString = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(likeEventChannel, valueAsString);
        log.info("Send LikeEvent to Brokers channel: {} , message: {}", message, likeEventChannel);
    }
}
