package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {

    public void checkingForCompliance(Comment comment, UpdateCommentRequest updateCommentRequest) {
        if (comment.getAuthorId() != updateCommentRequest.getAuthorId()) {
            throw new DataValidationException("immutable data has been changed");
        }
    }
}
