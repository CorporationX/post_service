package faang.school.postservice.service.comment;

import faang.school.postservice.dto.event.CommentEventDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.service.publisher.CommentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentActionService {

    private final CommentEventPublisher commentEventPublisher;

    public void registerNewComment(Comment comment) {
        CommentEventDto commentEventDto = CommentEventDto.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthorId())
                .text(comment.getContent())
                .build();
        commentEventPublisher.publish(commentEventDto);
    }
}
