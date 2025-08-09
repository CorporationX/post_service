package faang.school.postservice.publisher.user;

import faang.school.postservice.config.properties.RedisProperties;
import faang.school.postservice.publisher.MessagePublisher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("redisUserPublisher")
@RequiredArgsConstructor
public class UserBanPublisher implements MessagePublisher<String> {
    private static final String TOPIC_NAME = "user_ban_name";

    private final RedisProperties properties;
    private final RedisTemplate<String, Object> redisTemplate;
    private String topic;

    @PostConstruct
    private void init() {
        this.topic = properties.getChannels().get(TOPIC_NAME);
    }

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(topic, message);
        log.info("Message published. Banned user id: {}", message);
    }
}
