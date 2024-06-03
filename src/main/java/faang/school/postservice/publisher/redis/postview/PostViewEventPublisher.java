package faang.school.postservice.publisher.redis.postview;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewEvent;
import faang.school.postservice.publisher.redis.AbstractEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventPublisher extends AbstractEventPublisher<PostViewEvent> {

    public PostViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ChannelTopic postViewTopic,
                                  ObjectMapper objectMapper) {
        super(redisTemplate, postViewTopic, objectMapper);
    }
}
