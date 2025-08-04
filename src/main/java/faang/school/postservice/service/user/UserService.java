package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;
    public void checkUserExist(Long userId) {
        userServiceClient.checkUserExists(userId);
    }
}
