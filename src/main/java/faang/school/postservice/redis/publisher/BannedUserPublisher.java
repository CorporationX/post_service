package faang.school.postservice.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BannedUserPublisher implements MessagePublisher<String> {
    @Value("${redis.channels.user-ban}")
    String bannedUserTopic;

    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(bannedUserTopic, message);
    }
}
