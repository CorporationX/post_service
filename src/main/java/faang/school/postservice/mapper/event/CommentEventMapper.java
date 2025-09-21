package faang.school.postservice.mapper.event;

import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class CommentEventMapper {

    public CommentEvent toEvent(Comment comment, Post post) {
        return new CommentEvent(
                comment.getId(),
                comment.getAuthorId(),
                post.getId(),
                post.getAuthorId(),
                comment.getContent(),
                comment.getCreatedAt()
        );
    }
}