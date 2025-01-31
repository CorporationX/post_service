package faang.school.postservice.validator;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserExistValidator {
    private final UserServiceClient userServiceClient;

    private static final String USER_NOT_FOUND_ERR_MSG = "Пользователь с id:%s не найден!";

    public void userExist(long userId) {
        UserDto userResponse = userServiceClient.getUser(userId);
        log.debug("userResponse response: {}", userResponse);
        if (userResponse == null || userResponse.id() == null) {
            throw new DataNotFoundException(String.format(USER_NOT_FOUND_ERR_MSG, userId));
        }
    }
}
