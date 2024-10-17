package faang.school.postservice.publisher;

import faang.school.postservice.model.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommentEventPublisher {

    private final ChannelTopic commentTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(CommentEvent commentEvent) {
        redisTemplate.convertAndSend(commentTopic.getTopic(), commentEvent);
        log.info("Comment event was sent");
    }
}
