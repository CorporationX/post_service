package faang.school.postservice.validator;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEditDto;
import faang.school.postservice.exceptions.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {
    public void validateIdIsNotNull(Long id) {
        if (id == null || id <= 0) {
            throw new DataValidationException("Id cannot be null");
        }
    }

    public void validateComment(CommentDto commentDto) {
        if (commentDto == null || commentDto.getContent().isBlank()) {
            throw new DataValidationException("Comment cannot be null or empty");
        }
    }

    public void validateComment(CommentEditDto commentEditDto) {
        if (commentEditDto == null || commentEditDto.getContent().isBlank()) {
            throw new DataValidationException("Comment cannot be null or empty");
        }
    }

    public void validateOwnerComment(Long authorId, Long userId) {
        if (!authorId.equals(userId)) {
            throw new DataValidationException("This is not your comment");
        }
    }
}
