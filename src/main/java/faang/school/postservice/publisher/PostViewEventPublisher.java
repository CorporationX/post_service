package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.PostViewEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventPublisher {

    @Value("${spring.data.redis.channels.postViewEventPublisher}")
    private String channel;

    private final RedisTemplate<String, Object> redisTemplate;

    public void published(PostViewEvent postViewEvent) {
        redisTemplate.convertAndSend(channel,postViewEvent);
    }
}
