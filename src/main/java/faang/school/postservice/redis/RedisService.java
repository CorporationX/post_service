package faang.school.postservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {

    @Value("${redis.topics.user_ban}")
    private String topicBanUsers;

    private final RedisTemplate<String, Object> redisTemplate;

    public void sendMessageToBanUsers(Long userId) {
        redisTemplate.convertAndSend(topicBanUsers, userId);
    }
}
