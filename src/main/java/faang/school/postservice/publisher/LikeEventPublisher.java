package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.redisPublisher.EventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class LikeEventPublisher extends EventPublisher<LikeEvent> {
    public LikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                              ObjectMapper objectMapper,
                              @Qualifier("likeEventTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}
