package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    @Value("${spring.data.redis.channels.like-channel.name}")
    private String likeChannel;

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getChannel() {
        return likeChannel;
    }
}
