package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEventRedis;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CommentEvenRedisPublisher {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.comment_channel}")
    private String channel;

    public void publish(CommentEventRedis commentEventRedis) {
        redisTemplate.convertAndSend(channel, commentEventRedis);
        log.info("Published comment event: {}", commentEventRedis);
    }
}

