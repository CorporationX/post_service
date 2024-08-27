package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEvent> {

    private final ChannelTopic commentTopic;

    public CommentEventPublisher(ObjectMapper objectMapper,
                                 RedisTemplate<String, Object> redisTemplate,
                                 ChannelTopic commentTopic) {
        super(objectMapper, redisTemplate);
        this.commentTopic = commentTopic;
    }

    @Override
    protected String getChannelTopic() {
        return commentTopic.getTopic();
    }
}
