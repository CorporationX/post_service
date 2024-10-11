package faang.school.postservice.service.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final RedisTemplate<String, Object> template;
    private final ChannelTopic likeEventTopic;

    public void publish(String message) {
        template.convertAndSend(likeEventTopic.getTopic(), message);
    }
}
