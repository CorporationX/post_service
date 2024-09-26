package faang.school.postservice.validator;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {

    public void checkGetCreatedAtForZero(CommentDto commentDto) {
        if (commentDto.getCreatedAt() == null) {
            throw new DataValidationException("CreatedAt is null");
        }
    }

    public void checkingForCompliance(Comment comment, CommentDto commentDto) {
        if (comment.getAuthorId() != commentDto.getAuthorId() &&
                comment.getCreatedAt() != commentDto.getCreatedAt() &&
                comment.getUpdatedAt() != commentDto.getUpdatedAt()) {
            throw new DataValidationException("immutable data has been changed");
        }
    }

    public void validateCommentDto(CommentDto commentDto) {
        if (commentDto == null) {
            throw new DataValidationException("The CommentDto is null");
        }

        if (commentDto.getContent() == null) {
            throw new DataValidationException("The content is null");
        }

        if (commentDto.getContent().length() > 4096 || commentDto.getContent().isBlank()) {
            throw new DataValidationException("The content contains more than 4096 characters or the content is empty");
        }

    }
}
