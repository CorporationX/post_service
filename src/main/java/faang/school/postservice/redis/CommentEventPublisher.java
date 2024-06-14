package faang.school.postservice.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Component
public class CommentEventPublisher extends AbstractEventPublisher {
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, @Qualifier("commentTopic") ChannelTopic topic) {
        super(redisTemplate, topic);
    }
}
