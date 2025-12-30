package faang.school.postservice.util.client;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceClientAdapter {

    private final UserServiceClient userServiceClient;

    public UserDto getUserById(long userId) {
        try {
            return userServiceClient.getUserById(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(String.format("User #%d is not found", userId));
        }
    }
}
