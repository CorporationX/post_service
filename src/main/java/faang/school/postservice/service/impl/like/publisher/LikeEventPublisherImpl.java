package faang.school.postservice.service.impl.like.publisher;

import faang.school.postservice.event.LikeEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisherImpl implements LikeEventPublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Value("${spring.data.redis.channel.like}")
    private String likeTopic;

    @Override
    public void publisher(LikeEventDto likeEventDto) {
        redisTemplate.convertAndSend(likeTopic, likeEventDto);
    }
}
