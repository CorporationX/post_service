package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher implements MessagePublisher<CommentEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Async
    @Override
    public void publishMessage(CommentEvent event) {
        redisTemplate.convertAndSend(topic.getTopic(), event);
    }
}
