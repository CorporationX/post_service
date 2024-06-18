package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventPublisher extends AbstractEventPublisher<PostViewEvent> {
    private final ChannelTopic postViewEventTopic;

    @Autowired
    public PostViewEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic postViewEventTopic) {
        super(redisTemplate, objectMapper);
        this.postViewEventTopic = postViewEventTopic;
    }

    public void publish(PostViewEvent postViewEvent) {
        publish(postViewEvent, postViewEventTopic);
    }
}
