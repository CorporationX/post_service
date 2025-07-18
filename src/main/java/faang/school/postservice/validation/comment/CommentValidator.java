package faang.school.postservice.validation.comment;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.stereotype.Component;

@Component
public class CommentValidator {

    public void ensurePostExists(boolean exists, Long postId) {
        if (!exists) {
            throw new EntityNotFoundException("Post not found with id: " + postId);
        }
    }

    public void ensureCommentBelongsToPost(Comment comment, Long postId) {
        if (!comment.getPost().getId().equals(postId)) {
            throw new DataValidationException("Comment does not belong to post with id: " + postId);
        }
    }

    public void ensureUserIsAuthor(Long authorId, Long commentAuthorId) {
        if (!authorId.equals(commentAuthorId)) {
            throw new ForbiddenException("User is not the author of this comment");
        }
    }

    public void ensureUserCanDeleteComment(Comment comment, Post post, Long userId) {
        boolean isAuthorOfComment = comment.getAuthorId().equals(userId);
        boolean isAuthorOfPost = post.getAuthorId().equals(userId);

        if (!isAuthorOfComment && !isAuthorOfPost) {
            throw new ForbiddenException("User is not allowed to delete this comment");
        }
    }
}
