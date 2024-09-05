package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {

    private final ChannelTopic commentEventTopic;

    @Autowired
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper, ChannelTopic commentEventTopic) {
        super(redisTemplate, objectMapper);
        this.commentEventTopic = commentEventTopic;
    }

    public void publish(CommentEvent commentEvent) {
        publish(commentEvent, commentEventTopic);
    }
}
