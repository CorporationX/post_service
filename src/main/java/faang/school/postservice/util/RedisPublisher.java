package faang.school.postservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisPublisher {
    private final RedisTemplate<String, String> redisTemplate;

    public void publishMessage(String channel, String message) {
        redisTemplate.convertAndSend(channel, message);
    }
}
