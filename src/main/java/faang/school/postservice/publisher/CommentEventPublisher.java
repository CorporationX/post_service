package faang.school.postservice.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentEventDto> {
    private final ChannelTopic topicCommentEvent;
    private final CommentMapper commentMapper;
    @Autowired
    public CommentEventPublisher(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, ChannelTopic topicCommentEvent, CommentMapper commentMapper) {
        super(redisTemplate, objectMapper);
        this.topicCommentEvent = topicCommentEvent;
        this.commentMapper = commentMapper;
    }
    public void publish(Comment comment) {
        CommentEventDto event = commentMapper.toEventDto(comment);
        event.setReceivedAt(LocalDateTime.now());
        publishInTopic(topicCommentEvent, event);
    }
}
