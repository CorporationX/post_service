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
            throw new IllegalArgumentException("You can't modify id of comment");
        }

        if (updatedComment.getAuthorId() != null) {
            throw new IllegalArgumentException("You can't modify author id of comment");
        }

        if (updatedComment.getLargeImageFileKey() != null) {
            throw new IllegalArgumentException("You can't modify large image file key of comment");
        }

        if (updatedComment.getSmallImageFileKey() != null) {
            throw new IllegalArgumentException("You can't modify small image file key of the comment");
        }
    }

    public void validateAuthor(Comment comment, Long userId) {
        if (!Objects.equals(userId, comment.getAuthorId())) {
            throw new IllegalArgumentException("You can't modify comment of another user");
        }
    }

}
