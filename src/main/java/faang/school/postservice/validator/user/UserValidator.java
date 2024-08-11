package faang.school.postservice.validator.user;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public boolean isUserExists(Long userId) {
        return userServiceClient.getUser(userId) != null;
    }
}
