package faang.school.postservice.service.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(String topic, String message) {
        redisTemplate.convertAndSend(topic, message);
    }
}
