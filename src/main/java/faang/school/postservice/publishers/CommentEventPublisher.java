package faang.school.postservice.publishers;

import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.Event;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher extends AbstractMessagePublisher<Comment, CommentEvent> {
    private final CommentMapper commentMapper;

    public CommentEventPublisher(ChannelTopic postCommentChannel,
                                 RedisTemplate<String, Event> redisTemplate,
                                 CommentMapper commentMapper) {
        super(postCommentChannel, redisTemplate);
        this.commentMapper = commentMapper;
    }

    @Override
    CommentEvent mapper(Comment comment) {
        return commentMapper.toEvent(comment);
    }
}
