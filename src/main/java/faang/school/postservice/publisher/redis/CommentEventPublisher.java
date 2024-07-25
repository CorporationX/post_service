package faang.school.postservice.publisher.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.CommentEventDto;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Setter
@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {
    private final ChannelTopic commentEventTopic;

    @Autowired
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper,
                                 ChannelTopic commentEventTopic) {
        super(redisTemplate, objectMapper);
        this.commentEventTopic = commentEventTopic;
    }

    public void sendEvent(CommentEventDto event) {
        publish(commentEventTopic, event);
    }
}