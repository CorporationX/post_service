package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.PostViewEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewPublisher extends AbstractEventPublisher<PostViewEvent> {
    public PostViewPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                             @Qualifier("postViewTopic") ChannelTopic channelTopic) {
        super(redisTemplate, objectMapper, channelTopic);
    }
}


