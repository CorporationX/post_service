package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {
    private final CommentMapper commentMapper;

    @Autowired
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                 ObjectMapper objectMapper,
                                 CommentMapper commentMapper,
                                 @Value("${spring.data.redis.channels.comment_event_channel}") String commentEventChannelName) {
        super(redisTemplate, objectMapper, commentEventChannelName);
        this.commentMapper = commentMapper;
    }

    public void publish(Comment comment) {
        CommentEventDto event = commentMapper.toEventDto(comment);
        event.setReceivedAt(LocalDateTime.now());
        publishInTopic(event);
    }
}
