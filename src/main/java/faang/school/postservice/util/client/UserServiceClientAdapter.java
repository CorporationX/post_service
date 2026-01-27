package faang.school.postservice.util.client;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
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

    public List<Long> getFollowersIds(long followeeId) {
        try {
            return userServiceClient.getFollowersIds(followeeId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException(String.format("User #%d is not found", followeeId));
        }
    }
}