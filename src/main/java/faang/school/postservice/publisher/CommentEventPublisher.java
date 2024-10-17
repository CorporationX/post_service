package faang.school.postservice.publisher;

import faang.school.postservice.model.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentReceivingTopic;

    public void publish(CommentEvent commentEvent) {
        redisTemplate.convertAndSend(commentReceivingTopic.getTopic(), commentEvent);
    }
}
