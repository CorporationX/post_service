package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostViewEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;
    private final ObjectMapper objectMapper;

    public void publish(PostViewEvent event) {
        try {
            String eventAsJson = objectMapper.writeValueAsString(event);

            redisTemplate.convertAndSend(redisProperties.getPostViewChannel(), eventAsJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize PostViewEvent", e);
        }
    }
}
