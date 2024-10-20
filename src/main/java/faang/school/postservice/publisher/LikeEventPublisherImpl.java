package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.LikeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LikeEventPublisherImpl implements MessagePublisher<LikeEvent> {

    private final RedisTemplate<String, LikeEvent> redisTemplate;
    private final ChannelTopic likeEventTopic;

    public LikeEventPublisherImpl(RedisTemplate<String, LikeEvent> redisTemplate,
                                  @Qualifier("likeChannel") ChannelTopic likeEventTopic) {
        this.redisTemplate = redisTemplate;
        this.likeEventTopic = likeEventTopic;
    }

    @Override
    public void publish(LikeEvent event) {
        try {
            redisTemplate.convertAndSend(likeEventTopic.getTopic(), event);
            log.info("Publish like event {}", event);
        } catch (Exception e) {
            log.error("Failed to publish like event {}", event, e);
            throw new RuntimeException(e);
        }
    }
}
