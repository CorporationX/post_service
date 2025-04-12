package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractListEventPublisher {

    protected final RedisTemplate<String, Object> redisTemplate;
    protected abstract String getChannel();

    public void publish(LikeEvent likeEvent) {
        redisTemplate.convertAndSend(getChannel(), likeEvent);
        log.debug("Published LikeEvent to channel {}: {}", getChannel(), likeEvent);
    }
}
