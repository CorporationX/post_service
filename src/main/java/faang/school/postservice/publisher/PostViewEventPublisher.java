package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.post.PostViewEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher implements MessagePublisher<PostViewEventDto>{
    @Value("${spring.data.redis.channels.post_view_channel.name}")
    private String topic;

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(PostViewEventDto postViewEventDto){
        String json = null;
        try {
            json = objectMapper.writeValueAsString(postViewEventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        redisTemplate.convertAndSend(topic, json);
    }
}
