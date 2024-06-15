package faang.school.postservice.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher {
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public void publish(Object message) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), objectMapper.writeValueAsString(message));
        } catch (JsonProcessingException e) {
            log.error("Serializing to json failed: {0}", e);
            throw new RuntimeException(e);
        }
    }
}
