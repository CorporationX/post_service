package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class AdBoughtEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic adBoughtTopic;

    public void publish(String message) {
        redisTemplate.convertAndSend(adBoughtTopic.getTopic(), message);
    }
}
