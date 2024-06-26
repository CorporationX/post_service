package faang.school.postservice.publisher.redis.userban;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.event.UserEvent;
import faang.school.postservice.publisher.redis.AbstractEventRedisPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class UserBanRedisPublisher extends AbstractEventRedisPublisher<UserEvent> {


    public UserBanRedisPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ChannelTopic userBanTopic,
                                 ObjectMapper objectMapper) {
        super(redisTemplate, userBanTopic, objectMapper);
    }
}
