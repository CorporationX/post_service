package faang.school.postservice.config.redis;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class LikeEventPublisher {

    private final RedisTemplate<String, LikeEvent> redisTemplate;
    private final ChannelTopic likeEventTopic;

    public void publish(String message) {
        redisTemplate.convertAndSend(likeEventTopic.getTopic(), message);
    }

}
