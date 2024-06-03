package faang.school.postservice.publisher.redis.userban;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.UserEvent;
import faang.school.postservice.publisher.redis.AbstractEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class UserBanPublisher extends AbstractEventPublisher<UserEvent> {


    public UserBanPublisher(RedisTemplate<String, Object> redisTemplate,
                            ChannelTopic userBanTopic,
                            ObjectMapper objectMapper) {
        super(redisTemplate, userBanTopic, objectMapper);
    }
}
