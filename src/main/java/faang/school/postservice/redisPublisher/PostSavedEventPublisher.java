package faang.school.postservice.redisPublisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.Post;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostSavedEventPublisher extends EventPublisher<Post> {
    public PostSavedEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                   ObjectMapper objectMapper,
                                   @Qualifier("postSavedChannelTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}
