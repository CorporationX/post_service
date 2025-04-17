package faang.school.postservice.publisher.like;

import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.model.outbox.EventType;
import faang.school.postservice.publisher.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeleteLikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    @Value("${spring.data.redis.channels.not-like-channel.name}")
    private String notLikeChannel;

    public DeleteLikeEventPublisher(RedisTemplate<String, Object> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String getChannel() {
        return notLikeChannel;
    }

    @Override
    public EventType getEventType() {
        return EventType.LIKE_DELETED;
    }

    @Override
    public Class<LikeEvent> getEventClass() {
        return LikeEvent.class;
    }
}
