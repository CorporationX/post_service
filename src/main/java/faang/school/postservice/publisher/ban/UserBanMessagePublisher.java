package faang.school.postservice.publisher.ban;

import faang.school.postservice.event.ban.UserBanEvent;
import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserBanMessagePublisher implements MessagePublisher<UserBanEvent> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;

    @Override
    public void publish(UserBanEvent message) {
        redisTemplate.convertAndSend(userBanTopic.getTopic(), message);
        log.info("Message was send {}, in topic - {}", message, userBanTopic.getTopic());
    }
}
