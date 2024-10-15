package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.UserNotFoundException;
import feign.FeignException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    @Retryable(retryFor = FeignException.class, maxAttempts = 4, backoff = @Backoff(delay = 5000))
    public void validateUserExists(long userId) {
        try {
            userServiceClient.getUser(userId);
        } catch (ExternalServiceException e) {
            if (e.getStatus().equals(HttpStatus.NOT_FOUND)) {
                throw new UserNotFoundException("User with ID " + userId + " not found.");
            } else {
                throw e;
            }
        }
    }
}
