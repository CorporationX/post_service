package faang.school.postservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserBanPublisher {
    private final StringRedisTemplate redisTemplate;
    private final ChannelTopic topic;

    public void publishUserBan(Long userId) {
        redisTemplate.convertAndSend(topic.getTopic(), userId.toString());
    }
}

