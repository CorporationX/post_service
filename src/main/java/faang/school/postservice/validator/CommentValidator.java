package faang.school.postservice.validator;

import faang.school.postservice.exceptions.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {
    public void validateIdIsNotLessOne(Long id) {
        if ( id <= 0) {
            throw new DataValidationException("Id cannot be null");
        }
    }

    public void validateContent(String content) {
        if (content.isBlank()) {
            throw new DataValidationException("Comment cannot be null or empty");
        }
    }


    public void checkOwnerComment(Long authorId, Long userId) {
        if (!authorId.equals(userId)) {
            throw new DataValidationException("This is not your comment");
        }
    }
}
