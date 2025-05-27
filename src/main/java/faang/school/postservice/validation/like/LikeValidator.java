package faang.school.postservice.validation.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.like.LikeAlreadyExistsException;
import faang.school.postservice.exception.user.UserNotFoundException;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class LikeValidator {
    private LikeRepository likeRepository;
    private UserServiceClient userServiceClient;

    public void checkLikeAuthorExists(long userId) {
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            log.error("User with id = {} not found", userId);
            throw new UserNotFoundException(e.getMessage());
        }
    }

    public void checkUserHasNoLikeOnPost(long userId, long postId) {
        likeRepository.findByPostIdAndUserId(postId, userId)
                .ifPresent(like -> {
                    log.error("User id = {} has already liked post id = {}", userId, postId);
                    throw new LikeAlreadyExistsException(String.format(
                            "User id = %d has already liked post id = %d", userId, postId));
                });
    }

    public void checkUserHasNoLikeOnComment(long userId, long commentId) {
        likeRepository.findByCommentIdAndUserId(commentId, userId)
                .ifPresent(like -> {
                    log.error("User id = {} has already liked comment id = {}", userId, commentId);
                    throw new LikeAlreadyExistsException(String.format(
                            "User id = %d has already liked comment id = %d", userId, commentId));
                });
    }
}
