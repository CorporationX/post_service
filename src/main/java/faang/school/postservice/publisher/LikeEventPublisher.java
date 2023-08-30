package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
@Component
public class LikeEventPublisher{
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.channels.like_channel.name}")
    private String likeChannel;

    public void publish(LikeEvent message) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(message);
        redisTemplate.convertAndSend(likeChannel, json);
    }
}