package faang.school.postservice.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postViewTopic;
    private final ObjectMapper objectMapper;

    public void publish(PostViewEvent postViewEvent) throws JsonProcessingException {
        redisTemplate.convertAndSend(postViewTopic.getTopic(), objectMapper.writeValueAsString(postViewEvent));
    }
}
