package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractEventPublisher<T> {

    protected final RedisTemplate<String, Object> redisTemplate;
    protected abstract String getChannel();

    public void publish(T event) {
        redisTemplate.convertAndSend(getChannel(), event);
        log.debug("Published {} to channel {}: {}",event.getClass(), getChannel(), event);
    }
}
