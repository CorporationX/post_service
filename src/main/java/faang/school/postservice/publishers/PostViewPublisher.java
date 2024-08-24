package faang.school.postservice.publishers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Data
@Slf4j
public class PostViewPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postViewChannel;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(postViewChannel.getTopic(), message);
    }
}
