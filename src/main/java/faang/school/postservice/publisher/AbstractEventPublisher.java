package faang.school.postservice.publisher;

import faang.school.postservice.config.context.UserContext;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
@Component
public abstract class AbstractEventPublisher<T, K> {
    private final RedisTemplate<String, T> eventRedisTemplate;
    private final UserContext userContext;

    public void sendEntityToAnalytics(K entity, String viewChannel) {
        log.info("Entering publishPostEvent advice. Return value: {}", entity);
        long actorId = userContext.getUserId();
        log.info("Actor id = {}", actorId);
        T event = createEvent(entity, actorId);
        eventRedisTemplate.convertAndSend(viewChannel, event);
        log.info("Message published {} to topic {}", event, viewChannel);
    }

    public abstract T createEvent(K entity, Long actorId);
}
