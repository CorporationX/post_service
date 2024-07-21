package faang.school.postservice.service.like.validation;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeServiceValidator {

    private final UserServiceClient userServiceClient;

    public void validateByUserAndPostLikePossibility(Post post, Long userId) {
        validateUser(userId);
        validateLikingPostLogicalPossibility(post, userId);
    }

    public void validateByUserAndCommentLikePossibility(Comment comment, Long userId) {
        validateUser(userId);
        validateLikingCommentLogicalPossibility(comment, userId);
    }

    private void validateUser(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            if (e.status() == 500) {
                log.info("User with id {} does not exist", userId);
                throw new EntityNotFoundException("User with id " + userId + " does not exist");
            }
        }
    }

    private void validateLikingPostLogicalPossibility(Post post, Long userId) {
        if (post.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Post with id {} already liked by user {}", post.getId(), userId);
            throw new ValidationException("Post with id " + post.getId() + " already liked by user " + userId);
        }
        if (post.getComments().stream()
                .flatMap(comment -> comment.getLikes().stream())
                .anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Post with id {} already has comment liked by user {}", post.getId(), userId);
            throw new ValidationException("Post with id " + post.getId() + " already has comment liked by user " + userId);
        }
    }

    private void validateLikingCommentLogicalPossibility(Comment comment, Long userId) {
        if (comment.getLikes().stream().anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Comment with id {} already liked by user {}", comment.getId(), userId);
            throw new ValidationException("Comment with id " + comment.getId() + " already liked by user " + userId);
        }
        if (comment.getPost().getLikes().stream()
                .anyMatch(like -> like.getUserId().equals(userId))) {
            log.info("Comment with id {} belongs to post liked by user {}", comment.getId(), userId);
            throw new ValidationException("Comment with id " + comment.getId() + " belongs to post liked by user " + userId);
        }
    }
}
