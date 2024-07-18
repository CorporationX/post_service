package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public void validateUserExistence(long userId) {
        if (!userServiceClient.checkUserExistence(userId)) {
            String errMessage = String.format("User with ID: %d was not found in Database", userId);
            log.info(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
    }
}