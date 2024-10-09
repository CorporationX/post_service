package faang.school.postservice.config.scheduler;

import faang.school.postservice.config.properties.RedisProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {

    private final StringRedisTemplate redisTemplate;
    private final RedisProperties redisProperties;

    public void publish(String message) {
        redisTemplate.convertAndSend(redisProperties.getName(), message);
    }
}
