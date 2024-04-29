package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.PostEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class PostEventPublisher extends AbstractEventPublisher<PostEvent> {
    @Value("${spring.data.redis.channels.post_channel.name}")
    private String postChannel;

    public PostEventPublisher(ObjectMapper objectMapper, RedisTemplate<String, Object> redisTemplate) {
        super(objectMapper, redisTemplate);
    }

    public void publish(PostEvent postEvent) {
        convert(postEvent, postChannel);
    }
}
