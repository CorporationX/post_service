package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeValidator {
    private final UserServiceClient userServiceClient;

    public void validateUserExistence(LikeDto likeDto) {
        Long userId = likeDto.getUserId();
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException e) {
            throw new DataNotFoundException("Cant find user with id " + userId);
        }
    }
}
