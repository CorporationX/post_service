package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikePostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final RedisTemplate<String, LikePostEvent> redisTemplate;
    private final ChannelTopic postLikeChannel;

    public void publish(LikePostEvent event) {
        redisTemplate.convertAndSend(postLikeChannel.getTopic(), event);
    }
}
