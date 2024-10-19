package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.comment_channel.name}")
    private String topicName;

    public void publish(CommentEvent event) {
        redisTemplate.convertAndSend(topicName, event);
    }
}
