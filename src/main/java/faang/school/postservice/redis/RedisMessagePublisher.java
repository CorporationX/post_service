package faang.school.postservice.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.BanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RedisMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(userBanTopic.getTopic(), message);
    }

    public void createJson(long authorId) {
        try {
        BanEvent banEvent = new BanEvent();
        banEvent.setAuthorId(authorId);
        publish(objectMapper.writeValueAsString(banEvent));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}