package faang.school.postservice.publisher.redis;

import faang.school.postservice.event.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeTopic;

    public void publish(LikeEvent event) {
        redisTemplate.convertAndSend(likeTopic.getTopic(), event);
    }
}
