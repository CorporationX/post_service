package faang.school.postservice.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Component
public class LikeEventPublisher extends AbstractEventPublisher {
    public LikeEventPublisher(ObjectMapper objectMapper,
                              RedisTemplate<String, Object> redisTemplate,
                              @Qualifier("likeTopic") ChannelTopic topic) {
        super(objectMapper, redisTemplate, topic);
    }
}
