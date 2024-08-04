package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final UserServiceClient userServiceClient;

    public void validateUserAndPost(Post post, Long userId) {
        validateUser(userId);
        validatePostNotLikedYet(post, userId);
        validatePostHaveNoCommentLikedYet(post, userId);
    }

    public void validateUserAndComment(Comment comment, Long userId) {
        validateUser(userId);
        validateCommentNotLikedYet(comment, userId);
        validateCommentNotBelongsToLikedPost(comment, userId);
    }

    private void validateUser(Long userId) {
            userServiceClient.getUser(userId);
    }

    private void validatePostNotLikedYet(Post post, Long userId) {
        if (post.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Post with id {} already liked by user {}", post.getId(), userId);
            throw new DataValidationException("Post with id " + post.getId() + " already liked by user " + userId);
        }
    }

    private void validatePostHaveNoCommentLikedYet(Post post, Long userId) {
        if (post.getComments().stream()
                .flatMap(comment -> comment.getLikes().stream())
                .anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Post with id {} already has comment liked by user {}", post.getId(), userId);
            throw new DataValidationException("Post with id " + post.getId() + " already has comment liked by user " + userId);
        }
    }

    private void validateCommentNotLikedYet(Comment comment, Long userId) {
        if (comment.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Comment with id {} already liked by user {}", comment.getId(), userId);
            throw new DataValidationException("Comment with id " + comment.getId() + " already liked by user " + userId);
        }
    }

    private void validateCommentNotBelongsToLikedPost(Comment comment, Long userId) {
        if (comment.getPost().getLikes().stream()
                .anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Comment with id {} belongs to post liked by user {}", comment.getId(), userId);
            throw new DataValidationException("Comment with id " + comment.getId() + " belongs to post liked by user " + userId);
        }
    }
}
