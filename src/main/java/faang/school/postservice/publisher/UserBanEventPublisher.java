package faang.school.postservice.publisher;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserBanEventPublisher implements MessagePublisher<Long> {

    private final RedisTemplate<String, Long> redisTemplate;
    private final ChannelTopic topic;

    public UserBanEventPublisher(RedisTemplate<String, Long> redisTemplate,
                                 @Qualifier("banChannel") ChannelTopic topic) {
        this.redisTemplate = redisTemplate;
        this.topic = topic;
    }

    @Retryable(retryFor = {RuntimeException.class}, backoff = @Backoff(delayExpression = "${retryable.delay}"))
    @Override
    public void publish(Long userId) {
        try {
            redisTemplate.convertAndSend(topic.getTopic(), userId);
            log.info("publishUserToBan: userId = {}", userId);
        } catch (Exception e) {
            log.error("Failed to publish user with {} to ban", userId, e);
            throw new RuntimeException(e);
        }
    }
}
