package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.UserBanEventDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserBanEventPublisher extends AbstractEventPublisher<UserBanEventDto> {

    public UserBanEventPublisher(ObjectMapper objectMapper,
                                 RedisTemplate<String, Object> redisTemplate,
                                 @Value("${spring.data.redis.channel.user_ban.name}")String topic) {
        super(objectMapper, redisTemplate, topic);
    }

    @Override
    public void publishInTopic(UserBanEventDto event) {
        super.publishInTopic(event);
        log.info("UserBanEventDto published with userId = {}", event.getUserId());
    }
}
