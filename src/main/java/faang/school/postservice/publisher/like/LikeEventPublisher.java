package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEventDto;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventPublisher implements MessagePublisher<LikeEventDto> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeEventsTopic;

    @Override
    public void publish(LikeEventDto message) {
        redisTemplate.convertAndSend(likeEventsTopic.getTopic(), message);
    }
}
