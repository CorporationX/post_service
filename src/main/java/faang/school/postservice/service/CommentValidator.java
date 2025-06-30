package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class CommentValidator {

    public void validateAuthor(Comment comment, Long userId) {
        if (!comment.getAuthorId().equals(userId)) {
            throw new DataValidationException("Only the author can modify or delete this comment.");
        }
    }

    public void validateCommentCreate(CommentDto dto) {
        if (!StringUtils.hasText(dto.getContent())) {
            throw new DataValidationException("Comment content must not be empty.");
        }
        if (dto.getContent().length() > 4096) {
            throw new DataValidationException("Comment content must be less than 4096 characters.");
        }
        if (dto.getAuthorId() == null) {
            throw new DataValidationException("Comment must have an author.");
        }
        if (dto.getPostId() == null) {
            throw new DataValidationException("Comment must be linked to a post.");
        }
    }

    public void validateCommentUpdate(CommentDto dto) {
        if (!StringUtils.hasText(dto.getContent())) {
            throw new DataValidationException("Comment content must not be empty.");
        }
        if (dto.getContent().length() > 4096) {
            throw new DataValidationException("Comment content must be less than 4096 characters.");
        }
    }
}