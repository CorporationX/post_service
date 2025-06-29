package faang.school.postservice.service;

import faang.school.postservice.model.Comment;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@NoArgsConstructor
@Service
public class CommentValidator {

    public void validateCommentUpdate(Comment updatedComment) {
        if (updatedComment.getId() != null) {
            throw new IllegalArgumentException("You can't modify id of the comment");
        }

        if (updatedComment.getAuthorId() != null) {
            throw new IllegalArgumentException("You can't modify authorId of the comment");
        }

        if (updatedComment.getPost() != null) {
            throw new IllegalArgumentException("You can't modify post of the comment");
        }
    }

    public void validateAuthor(Comment comment, Long userId) {
        if (!Objects.equals(userId, comment.getAuthorId())) {
            throw new IllegalArgumentException("You can't modify a comment authored by another user");
        }
    }
}