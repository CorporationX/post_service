package faang.school.postservice.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.events.CommentEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.data.redis.channels.name.comment_event}")
    private String channel;

    public void publish(CommentEvent commentEvent) {
        log.info("Publishing comment event notification");
        redisTemplate.convertAndSend(channel, commentEvent);
        log.info("Comment event notification sent");
    }
}