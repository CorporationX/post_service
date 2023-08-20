package faang.school.postservice.redis.publisher;

import faang.school.postservice.redis.event.LikeEvent;
import faang.school.postservice.redis.topic.LikeTopic;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;

@RequiredArgsConstructor
@Component
public class LikeEventPublisher{
    private final RedisTemplate<String, Object> redisTemplate;
    private final LikeTopic likeTopic;
    public void publish(LikeEvent message) {
        redisTemplate.convertAndSend(likeTopic.topic().getTopic(), message);
    }
}
