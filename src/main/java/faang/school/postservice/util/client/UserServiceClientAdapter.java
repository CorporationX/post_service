package faang.school.postservice.util.client;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceClientAdapter {

    private final UserServiceClient userServiceClient;

    public void getUserById(long userId) {
        try {
            userServiceClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(e.getMessage());
        }
    }
}
