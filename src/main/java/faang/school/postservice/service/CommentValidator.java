package faang.school.postservice.service;

import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.model.Comment;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {

    public void validateAuthor(Comment comment, Long userId) {
        if (!comment.getAuthorId().equals(userId)) {
            throw new CommentValidationException("Only the author can modify or delete this comment.");
        }
    }
}