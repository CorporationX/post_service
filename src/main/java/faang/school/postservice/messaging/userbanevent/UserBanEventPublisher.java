package faang.school.postservice.messaging.userbanevent;

import faang.school.postservice.messaging.EventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserBanEventPublisher implements EventPublisher<Long> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    @Override
    public void publish(Long userId) {
        redisTemplate.convertAndSend(topic.getTopic(), userId);
        log.info(userId + " was sent");
    }
}
