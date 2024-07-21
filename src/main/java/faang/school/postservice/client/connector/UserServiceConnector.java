package faang.school.postservice.client.connector;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.annotation.Repeatable;

@Service
@RequiredArgsConstructor
public class UserServiceConnector {
    private final UserServiceClient userServiceClient;


    public UserDto getUserById(long userId) {
        return userServiceClient.getUser(userId);
    }
}
