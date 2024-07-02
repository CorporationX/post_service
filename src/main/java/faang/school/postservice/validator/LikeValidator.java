package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeValidator {

    private final LikeRepository likeRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public void likeValidate(long postId, long userId) {
        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(like -> {
            String errMessage = String.format("User ID: %d already liked post ID: %d", userId, postId);
            log.info(errMessage);
            throw new DataValidationException(errMessage);
        });
        userContext.setUserId(userId);
        userServiceClient.getUser(userId);
    }
}
