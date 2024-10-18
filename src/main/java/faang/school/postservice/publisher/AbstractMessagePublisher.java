package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractMessagePublisher implements MessagePublisher {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public void publish(String message) {
        try {
            stringRedisTemplate.convertAndSend(getTopicName(), message);
        } catch (Exception exception) {
            log.error("Failed to send message to Redis", exception);
        }
    }

    protected abstract String getTopicName();
}
