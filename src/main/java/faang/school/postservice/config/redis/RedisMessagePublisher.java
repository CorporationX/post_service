package faang.school.postservice.config.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisMessagePublisher implements MessagePublisher {

    private final RedisTemplate<String, Object> template;

    @Override
    public void publish(String channel, Object message) {
        log.info("Message sent to channel {}, {}", channel, message);
        template.convertAndSend(channel, message);
    }
}
