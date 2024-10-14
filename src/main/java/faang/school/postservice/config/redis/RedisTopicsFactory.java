package faang.school.postservice.config.redis;

import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Component
public class RedisTopicsFactory {
    public Topic getTopic(String topicName) {
        return new ChannelTopic(topicName);
    }
}
