package faang.school.postservice.publisher;

import faang.school.postservice.event.BanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisBanMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;

    public void publish(BanEvent banEvent) {
        redisTemplate.convertAndSend(userBanTopic.getTopic(), banEvent);
    }
}
