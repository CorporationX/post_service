package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exceptions.DataNotFoundException;
import faang.school.postservice.exceptions.SameTimeActionException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final UserServiceClient userServiceClient;

    public void validateLike(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        Long postId = likeDto.getPostId();
        Long commentId = likeDto.getCommentId();
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataNotFoundException(String.format("User with id=%d doesn't exist", userId));
        }
        if (postId != null && commentId != null) {
            throw new SameTimeActionException("Can't add like on post and comment in the same time");
        }
    }
}
