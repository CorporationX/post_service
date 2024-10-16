package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.BanedUserEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class BanedUserEventPublisher extends AbstractEventPublisher<BanedUserEvent> {
    public BanedUserEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                   ObjectMapper objectMapper,
                                   @Qualifier("bannedUserTopic") ChannelTopic topic) {
        super(redisTemplate, objectMapper, topic);
    }
}