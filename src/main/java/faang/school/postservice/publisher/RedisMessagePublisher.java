package faang.school.postservice.publisher;

import faang.school.postservice.event.BanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic banTopic;

    public void publish(BanEvent banEvent) {
        redisTemplate.convertAndSend(banTopic.getTopic(), banEvent);
    }
}
