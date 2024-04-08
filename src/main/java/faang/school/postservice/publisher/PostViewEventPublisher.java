package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic postViewTopic;

    public void publish(PostViewEvent postViewEvent) {
        redisTemplate.convertAndSend(postViewTopic.getTopic(), postViewEvent);
    }
}
