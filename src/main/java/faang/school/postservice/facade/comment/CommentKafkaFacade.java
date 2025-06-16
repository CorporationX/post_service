package faang.school.postservice.facade.comment;

import faang.school.postservice.entity.comment.Comment;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.publisher.comment.CommentKafkaPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentKafkaFacade {
    private final CommentKafkaPublisher commentKafkaPublisher;

    public void createCommentEvent(Comment comment) {
        // TODO: в маппер
        // TODO: логи
        CommentEvent commentEvent = new CommentEvent();
        commentEvent.setId(comment.getId());
        commentEvent.setAuthorId(comment.getAuthorId());
        commentEvent.setPostId(comment.getPost().getId());
        commentEvent.setAuthorPostId(comment.getPost().getAuthorId());

        // TODO: кофига или enum
        commentKafkaPublisher.sendMessage(commentEvent);
    }
}
