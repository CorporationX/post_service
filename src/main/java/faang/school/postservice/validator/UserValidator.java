package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserValidator {
    private final UserServiceClient userServiceClient;

    public void checkUserExistence(Long authorId) {
        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException e) {
            log.error("User not found: {}", authorId, e);
            throw new EntityNotFoundException("User not found with id: " + authorId);
        }
    }
}