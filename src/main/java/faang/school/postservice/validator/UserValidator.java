package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public void validateUserExist(Long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (FeignException.InternalServerError e) {
            throw new EntityNotFoundException("User with id " + userId + " does not exists");
        }
    }
}
