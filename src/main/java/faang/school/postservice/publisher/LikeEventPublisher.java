package faang.school.postservice.publisher;

import faang.school.postservice.dto.like.LikeEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;


@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate, ChannelTopic topic) {
        super(redisTemplate, topic);
    }

    public void sendEvent(LikeEvent likeEvent){
        publish(likeEvent);
    }
}
