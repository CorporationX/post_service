package faang.school.postservice.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserBanEventPublisher implements MessagePublisher<Long> {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic topic;

    public UserBanEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 @Qualifier("banChannel") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Override
    public void publish(Long userId) {
        redisTemplate.convertAndSend(topic.getTopic(), userId);
        log.info("publishUserToBan: userId = {}", userId);
    }
}
