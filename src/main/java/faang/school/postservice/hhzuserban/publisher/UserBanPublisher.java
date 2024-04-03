package faang.school.postservice.hhzuserban.publisher;

import faang.school.postservice.hhzuserban.dto.message.UserBanMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

@RequiredArgsConstructor
public class UserBanPublisher implements MessagePublisher {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic userBanTopic;

    @Override
    public void publish(UserBanMessage userBanMessage) {
        redisTemplate.convertAndSend(userBanTopic.getTopic(), userBanMessage.getUserId());
    }
}
