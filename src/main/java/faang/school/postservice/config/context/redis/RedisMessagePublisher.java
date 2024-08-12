package faang.school.postservice.config.context.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Data
public class RedisMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
