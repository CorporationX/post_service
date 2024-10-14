package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final ChannelTopic likeChannelTopic;
    private final RedisTemplate<String, Object> redisTemplate;

    public void publish(LikeEvent likeEvent) {
        redisTemplate.convertAndSend(likeChannelTopic.getTopic(), likeEvent);
    }
}
