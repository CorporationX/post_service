package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.UserBanEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserBanPublisher extends AbstractEventPublisher<UserBanEvent> {

    private final ChannelTopic userBanChannel;

    public UserBanPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic userBanChannel) {
        super(redisTemplate, objectMapper);
        this.userBanChannel = userBanChannel;
    }

    public void sendUserBanEvent(UserBanEvent event) {
        publish(this.userBanChannel, event);
    }
}

