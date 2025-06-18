package faang.school.postservice.service.comment;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.publisher.comment.CommentEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class CommentActionService {

    private final CommentEventPublisher commentEventPublisher;
    private final CommentMapper commentMapper;

    public CommentActionService(@Qualifier("redisCommentEventPublisher") CommentEventPublisher commentEventPublisher,
                                CommentMapper commentMapper) {
        this.commentEventPublisher = commentEventPublisher;
        this.commentMapper = commentMapper;
    }

    public void registerNewComment(Comment comment) {
        CommentEventDto commentEventDto = commentMapper.toCommentEventDto(comment);
        commentEventPublisher.publish(commentEventDto);
    }
}
