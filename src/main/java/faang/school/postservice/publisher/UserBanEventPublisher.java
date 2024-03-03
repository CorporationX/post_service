package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.UserBanEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserBanEventPublisher extends AbstractEventPublisher<UserBanEvent> {
    @Value("${spring.data.redis.channel.user_ban.user_ban_channel}")
    private String userBanChannelName;

    public UserBanEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        super(redisTemplate, objectMapper);
    }

    @Override
    public void publish(UserBanEvent event) {
        convertAndSend(event, userBanChannelName);
    }
}
