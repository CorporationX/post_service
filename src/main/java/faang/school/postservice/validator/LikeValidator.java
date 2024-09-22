package faang.school.postservice.validator;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    public void validateLike(Like like, Post post) {
        if (isLikedByUser(like.getUserId(), post.getLikes())) {
            throw new DataValidationException("Post is already liked.");
        }
        for (Comment comment : post.getComments()) {
            if (isLikedByUser(like.getUserId(), comment.getLikes())) {
                throw new DataValidationException("Comment of post is already liked.");
            }
        }
    }

    public void validatePostAndCommentLikes(Post post, Like like) {
        if (isLikedById(like.getId(), post.getLikes())) {
            throw new DataValidationException("Post already liked");
        }
        for (Comment comment : post.getComments()) {
            if (isLikedById(like.getId(), comment.getLikes())) {
                throw new DataValidationException("Comment already liked");
            }
        }
    }

    private boolean isLikedByUser(Long userId, List<Like> likes) {
        return likes.stream().anyMatch(like -> like.getUserId().equals(userId));
    }

    private boolean isLikedById(Long likeId, List<Like> likes) {
        for (Like like : likes) {
            if (like.getId() == likeId) {
                return true;
            }
        }
        return false;
    }
}
