package faang.school.postservice.validation.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidation {

    private final UserServiceClient userServiceClient;

    public void doesUserExist(Long userId) {
        if (!userServiceClient.doesUserExist(userId)) {
            throw new DataValidationException(String.format("User with %s doesn't exist", userId));
        }
    }
}
