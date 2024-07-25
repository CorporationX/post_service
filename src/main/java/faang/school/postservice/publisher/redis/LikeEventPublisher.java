package faang.school.postservice.publisher.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@Setter
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    private final ChannelTopic likeEventTopic;

    @Autowired
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic likeEventTopic) {
        super(redisTemplate, objectMapper);
        this.likeEventTopic = likeEventTopic;
    }

    public void sendEvent(LikeEvent likeEvent) {
        publish(likeEventTopic, likeEvent);
    }
}
