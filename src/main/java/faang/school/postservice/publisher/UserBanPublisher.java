package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.UserEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class UserBanPublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public UserBanPublisher(RedisTemplate<String, Object> redisTemplate,
                            @Qualifier("userBanTopic") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    public void publish(UserEvent userEvent) {
        redisTemplate.convertAndSend(topic.getTopic(), userEvent);
    }
}
