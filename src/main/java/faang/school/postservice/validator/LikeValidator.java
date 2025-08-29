package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class LikeValidator {

    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;

    public void ensureUserExists(long userId){
        try{
            userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            log.warn("User with id={} not found: {}", userId, e.getMessage());
            throw new EntityNotFoundException("User not found with id: " + userId);
        }
    }

    public void ensureLikeOnPostAbsent(long postId, long userId){
        if (likeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new ForbiddenException(
                    String.format("Like on post already exists, userId: %s, postId: %s", userId, postId)
            );
        }
    }

    public void ensureLikeOnCommentAbsent(long commentId, long userId) {
        if (likeRepository.findByCommentIdAndUserId(commentId, userId).isPresent()){
            throw new ForbiddenException(
                    String.format("Like on comment already exists, userId: %s, commentId: %s", userId, commentId)
            );
        }
    }
}
