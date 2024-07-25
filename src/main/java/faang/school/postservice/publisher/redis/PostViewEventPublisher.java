package faang.school.postservice.publisher.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostViewEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class PostViewEventPublisher extends AbstractEventPublisher<PostViewEvent> {

    private final ChannelTopic postViewTopic;

    @Autowired
    public PostViewEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper,
                                  @Qualifier("postViewChannel") ChannelTopic postViewTopic) {

        super(redisTemplate, objectMapper);
        this.postViewTopic = postViewTopic;
    }

    public void sendEvent(PostViewEvent event) {
        publish(postViewTopic, event);
    }
}