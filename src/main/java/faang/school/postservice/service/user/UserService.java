package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserServiceClient userServiceClient;

    public UserDto getUser(long userId) {
        UserDto userDto = userServiceClient.getUser(userId);
        log.info("Get user {} from database", userDto);

        return userDto;
    }
}