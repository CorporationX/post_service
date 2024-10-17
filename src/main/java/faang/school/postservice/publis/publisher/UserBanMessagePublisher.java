package faang.school.postservice.publis.publisher;

import faang.school.postservice.config.redis.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBanMessagePublisher {
    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(String message) {
        redisTemplate.convertAndSend(redisProperties.getUserBanChannelName(), message);
    }
}
