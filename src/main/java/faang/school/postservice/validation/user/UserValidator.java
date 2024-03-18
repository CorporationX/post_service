package faang.school.postservice.validation.user;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserValidator {

    private final UserServiceClient userService;

    public void validateUserExist(Long userId) {
        try {
            userService.getUser(userId);
        } catch (FeignException.InternalServerError ex) {
            throw new EntityNotFoundException("User with id " + userId + " does not exists");
        }
    }
}
