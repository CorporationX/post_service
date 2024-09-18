package faang.school.postservice.validator.comment;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class CommentControllerValidator {
    public void validateCommentDtoNotNull(CommentDto commentDto) {
        if (commentDto == null) {
            throw new ValidationException("commentDto cannot be null");
        }
    }

    public void validateCommentPostIdNotNull(CommentDto commentDto) {
        if (commentDto.getPostId() == null) {
            throw new ValidationException("postId cannot be null");
        }
    }

    public void validateCommentContentNotNull(CommentDto commentDto) {
        if (commentDto.getContent() == null) {
            throw new ValidationException("content cannot be null");
        }
    }

    public void validateCommentAuthorIdNotNull(Long authorId) {
        if (authorId == null) {
            throw new ValidationException("authorId cannot be null");
        }
    }
}
