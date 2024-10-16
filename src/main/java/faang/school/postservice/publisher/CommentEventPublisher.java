package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {

    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 ChannelTopic commentTopic) {
        super(redisTemplate, objectMapper, commentTopic);
    }
}
