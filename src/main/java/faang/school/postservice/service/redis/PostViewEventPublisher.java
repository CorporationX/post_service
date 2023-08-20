package faang.school.postservice.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostViewEventPublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Object message) {
        log.info("PostViewEventPublisher is sending message to Redis");
        String jsonString = asJsonString(message);
        redisTemplate.convertAndSend(postTopic.getTopic(), jsonString);
    }

    private String asJsonString(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException while parsing message");
            throw new RuntimeException(e);
        }
    }
}
