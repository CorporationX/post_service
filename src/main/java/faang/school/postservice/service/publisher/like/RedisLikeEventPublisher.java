package faang.school.postservice.service.publisher.like;

import faang.school.postservice.dto.event.LikeEventDto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component("redisLikeEventPublisher")
public class RedisLikeEventPublisher implements LikeEventPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic likeTopic;

    public RedisLikeEventPublisher(
            RedisTemplate<String, Object> redisTemplate,
            @Qualifier("likeTopic") ChannelTopic likeTopic
    ) {
        this.redisTemplate = redisTemplate;
        this.likeTopic = likeTopic;
    }

    @Override
    public void publish(LikeEventDto event) {
        redisTemplate.convertAndSend(likeTopic.getTopic(), event);
    }
}
