package faang.school.postservice.publisher.user;

import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component("redisUserPublisher")
@RequiredArgsConstructor
public class UserBanPublisher implements MessagePublisher {
    @Qualifier(value = "redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier(value = "userTopic")
    private final ChannelTopic topic;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        log.info("Message published. Banned user id: {}", message);
    }
}
