package faang.school.postservice.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Component
public class LikeMessagePublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier("likeTopic")
    private final ChannelTopic topic;

    public LikeMessagePublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(Object message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
