package faang.school.postservice.publisher;

import faang.school.postservice.dto.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentEventPublisher {
    private final RedisTemplate<String, String> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(CommentEvent event) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), event);
    }
}
