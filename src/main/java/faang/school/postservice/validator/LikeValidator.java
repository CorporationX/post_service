package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exceptions.DataAlreadyExistingException;
import faang.school.postservice.exceptions.DataNotExistingException;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.exceptions.SameTimeActionException;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final UserServiceClient userServiceClient;
    private final LikeRepository likeRepository;

    public void validateLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long postId = likeDto.getPostId();
        Long commentId = likeDto.getCommentId();
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataNotExistingException("User who wants to add like doesn't exist");
        }
        if (postId == null && commentId == null) {
            throw new DataValidationException("PostId or CommentId is required");
        }
        if (postId != null && commentId != null) {
            throw new SameTimeActionException("Can't add like on post and comment in the same time");
        }
        if (postId != null) {
            likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(like -> {
                throw new DataAlreadyExistingException(String
                        .format("Like on postId: %d by user id: %d already exist", postId, userId));
            });
        }
        if (commentId != null) {
            likeRepository.findByCommentIdAndUserId(commentId, userId).ifPresent(like -> {
                throw new DataAlreadyExistingException(String
                        .format("Like on commentId: %d by user id: %d already exist", commentId, userId));
            });
        }
    }
}
