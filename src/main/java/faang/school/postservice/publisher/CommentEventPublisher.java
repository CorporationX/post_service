package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    private ChannelTopic commentEventTopic;

    public void publish(CommentEvent event) {
        redisTemplate.convertAndSend(commentEventTopic.getTopic(), event);
    }
}
