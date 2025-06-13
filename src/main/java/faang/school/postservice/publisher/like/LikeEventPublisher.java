package faang.school.postservice.publisher.like;

import faang.school.postservice.publisher.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component("likeEventPublisher")
@RequiredArgsConstructor
public class LikeEventPublisher implements MessagePublisher {
    @Qualifier(value = "redisTemplate")
    private final RedisTemplate<String, Object> redisTemplate;
    @Qualifier(value = "likeTopic")
    private final ChannelTopic topic;

    @Override
    public void publish(String message) {
        redisTemplate.convertAndSend(topic.getTopic(), message);
        log.info("Like event appeared. Like event: {}", message);
    }
}

