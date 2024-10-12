package faang.school.postservice.redisPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.event.PostLikeEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostLikeEventPublisher extends EventPublisher<PostLikeEvent> {
    public PostLikeEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Qualifier("postLikeChannelTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}
