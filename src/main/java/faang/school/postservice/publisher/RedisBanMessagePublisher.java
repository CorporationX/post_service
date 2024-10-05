package faang.school.postservice.publisher;

import faang.school.postservice.event.BanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisBanMessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(BanEvent banEvent) {
        redisTemplate.convertAndSend(channelTopic.getTopic(), banEvent);
    }
}
