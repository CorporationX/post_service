package faang.school.postservice.publisher;

import faang.school.postservice.properties.RedisProperties;
import faang.school.postservice.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisProperties redisProperties;

    @Retryable(
            value = RedisConnectionFailureException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void publish(CommentEvent event) {
        redisTemplate.convertAndSend(redisProperties.getChannels().getComment(), event);
    }

    @Recover
    public void recover(RedisConnectionFailureException e, CommentEvent event) {
        log.error("❌ Не удалось отправить CommentEvent после всех попыток: {}. Причина: {}", event, e.getMessage());
    }
}
