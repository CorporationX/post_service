package faang.school.postservice.publisher.like;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class NotLikeEventPublisher extends AbstractListEventPublisher{

    @Value("${spring.data.redis.channels.not-like-channel.name}")
    private String notLikeChannel;

    public NotLikeEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getChannel() {
        return notLikeChannel;
    }
}
