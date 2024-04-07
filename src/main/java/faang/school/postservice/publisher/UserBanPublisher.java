package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.UserEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBanPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;

    public void publish(UserEvent userEvent) {
        redisTemplate.convertAndSend(userBanTopic.getTopic(), userEvent);
    }
}
