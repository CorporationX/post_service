package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Setter
public class LikeEventPublisher implements MessagePublisher<LikeEvent> {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${spring.data.redis.channels.like_channel.name}")
    private String likeChannelName;

    public void publish(LikeEvent likeEvent) {
        redisTemplate.convertAndSend(likeChannelName, likeEvent);
    }
}
