package faang.school.postservice.publis.publisher.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.like.AbstractLikeEvent;
import faang.school.postservice.mapper.like.LikeEventMapper;
import faang.school.postservice.model.Like;
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
    private final LikeEventMapper likeEventMapper;

    public void publishPostLikeEventToBroker(Like like) {
        AbstractLikeEvent abstractLikeEvent = likeEventMapper.toPostLikeEvent(like);
        String postLikeEventChannel = redisProperties.getPostLikeEventChannelName();
        publish(abstractLikeEvent, postLikeEventChannel);
    }

    private void publish(Object message, String likeEventChannel) {
        String valueAsString;
        try {
            valueAsString = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(likeEventChannel, valueAsString);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        log.info("Send LikeEvent to Brokers channel: {} , message: {}", message, likeEventChannel);
    }
}
