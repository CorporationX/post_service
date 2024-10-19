package faang.school.postservice.publisher;

import faang.school.postservice.model.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postTopic;

    public void publish(PostViewEvent postEvent) {
        redisTemplate.convertAndSend(postTopic.getTopic(), postEvent);
    }
}
