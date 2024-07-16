package faang.school.postservice.publisher;

import faang.school.postservice.event.redis.UserBanRedisEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserBanEventPublisher implements RedisPublisher<UserBanRedisEvent> {

    @Value("${post.user-ban.channel.name}")
    private String topic;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void publish(UserBanRedisEvent event) {
        redisTemplate.convertAndSend(topic, event);
        log.info("Published user ban event - {}:{}", topic, event);
    }
}
