package faang.school.postservice.validator.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserServiceClient userServiceClient;

    public void checkUserExist(Long userId) {
        if (!userServiceClient.userExist(userId)) {
            throw new DataValidationException(String.format("User with %s doesn't exist", userId));
        }
    }
}
