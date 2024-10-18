package faang.school.postservice.publisher;

import faang.school.postservice.model.event.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postTopic;

    public void publish(PostEvent postEvent) {
        redisTemplate.convertAndSend(postTopic.getTopic(), postEvent);
    }
}
