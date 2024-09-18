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
        for (Like like1 : post.getLikes()) {
            if (like.getUserId().equals(like1.getUserId())) {
                throw new DataValidationException("Post is already liked.");
            }
        }
        for (Comment comment : post.getComments()) {
            for (Like like1 : comment.getLikes()) {
                if (like.getUserId().equals(like1.getUserId())) {
                    throw new DataValidationException("Comment of post is already liked.");
                }
            }
        }
    }

    public void validatePostAndCommentLikes(Post post, Like like) {
        List<Like> likesOfPost = post.getLikes();
        for (Like like1 : likesOfPost) {
            if (like1.getId() == (like.getId())) {
                throw new DataValidationException("Post already liked");
            }
        }
        List<Comment> commentsOfPost = post.getComments();
        for (Comment comment : commentsOfPost) {
            for (Like like1 : comment.getLikes()) {
                if (like1.getId() == like.getId()) {
                    throw new DataValidationException("Post or comment already liked");
                }
            }
        }
    }
}
