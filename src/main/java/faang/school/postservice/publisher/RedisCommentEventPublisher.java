package faang.school.postservice.publisher;

import faang.school.postservice.event.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisCommentEventPublisher implements CommentEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic commentTopic;

    @Override
    public void publish(CommentEvent event) {
        redisTemplate.convertAndSend(commentTopic.getTopic(), event);
    }
}
