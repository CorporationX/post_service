package faang.school.postservice.publisher.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventTopic;

    public void publish(Object message) {
        redisTemplate.convertAndSend(likeEventTopic.getTopic(), message);
    }

}
