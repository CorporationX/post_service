package faang.school.postservice.publisher;

import faang.school.postservice.model.dto.like.LikeEventDto;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Setter
@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEventDto> {
    private final ChannelTopic likeEventTopic;

    @Autowired
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ChannelTopic likeEventTopic) {
        super(redisTemplate);
        this.likeEventTopic = likeEventTopic;
    }

    public void sendEvent(LikeEventDto likeEvent) {
        publish(likeEventTopic, likeEvent);
    }
}
