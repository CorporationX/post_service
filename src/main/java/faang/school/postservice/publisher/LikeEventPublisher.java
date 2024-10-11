package faang.school.postservice.publisher;

import faang.school.postservice.event.LikeEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channels.like.name}")
    private String likeTopic;

    public void publisher(LikeEventDto likeEventDto) {
        redisTemplate.convertAndSend(likeTopic, likeEventDto);
    }
}
