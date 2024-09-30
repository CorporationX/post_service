package faang.school.postservice.messaging.publisher.redis.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.redis.like.LikeEvent;
import faang.school.postservice.messaging.publisher.redis.comment.AbstractEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends AbstractEventPublisher<LikeEvent> {

    public LikeEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate,
                              @Qualifier(value = "likeChannel") ChannelTopic channelTopic) {
        super(objectMapper, redisTemplate, channelTopic);
    }

    @Override
    public void publish(LikeEvent event) {
        super.publish(event);
    }
}