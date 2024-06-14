package faang.school.postservice.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Component
public class LikeEventPublisher extends AbstractEventPublisher {
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("likeTopic") ChannelTopic topic) {
        super(redisTemplate, topic);
    }
}
