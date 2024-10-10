package faang.school.postservice.publisher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisMessagePublisher implements MessagePublisher {
    private final StringRedisTemplate redisTemplate;

    @Value("${redis.banner.topic}")
    private String bannerTopic;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(bannerTopic, message);
        log.info("Message published: {}", message);
    }
}
