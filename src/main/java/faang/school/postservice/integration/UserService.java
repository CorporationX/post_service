package faang.school.postservice.integration;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserServiceClient userServiceClient;
    private final UserContext userContext;


    public UserDto getUser() {
        long userId = userContext.getUserId();
        try {
            return userServiceClient.getUser(userId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("User not found");
        }
    }
}

