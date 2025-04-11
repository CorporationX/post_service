package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.like-channel.name}")
    private String likeChannel;

    public void publish(LikeEvent likeEvent) {
        redisTemplate.convertAndSend(likeChannel, likeEvent);
        log.debug("Published LikeEvent to channel {}: {}", likeChannel, likeEvent);
    }
}
