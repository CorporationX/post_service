package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher {

    @Value("${spring.data.redis.channels.comment_channel}")
    private String channel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(CommentEvent commentEvent) {
        redisTemplate.convertAndSend(channel, commentEvent);
        log.info("Published comment event: {}", commentEvent);
    }
}
