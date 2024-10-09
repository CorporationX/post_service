package faang.school.postservice.publis.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(String message) {
        redisTemplate.convertAndSend(redisProperties.getCommentEventChannelName(), message);
        log.info("Sending message to NotificationService: " + message);
    }
}
