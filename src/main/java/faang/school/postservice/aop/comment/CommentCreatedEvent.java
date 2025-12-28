package faang.school.postservice.aop.comment;

import faang.school.postservice.model.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CommentCreatedEvent {
    private final Comment comment;
}
