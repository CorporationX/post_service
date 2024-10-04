package faang.school.postservice.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserIdsPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic;

    public void publish(Object obj) {
        if (obj instanceof List && !((List<?>) obj).isEmpty()) {
            redisTemplate.convertAndSend(channelTopic.getTopic(), obj);
        } else {
            log.warn("Empty or invalid user list to ban.");
        }
    }
}
