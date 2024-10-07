package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.topic.CommentEventTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher {
    private final RedisTemplate<String, CommentEvent> redisTemplate;
    private final CommentEventTopic topic;

    @Override
    public void publish(Object message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
