package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
public abstract class AbstractPublisher implements MessagePublisher {

    private final String topicName;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(Object message) {
        redisTemplate.convertAndSend(topicName, message);
    }
}
