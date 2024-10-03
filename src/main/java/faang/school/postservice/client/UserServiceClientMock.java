package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserServiceClientMock implements UserServiceClient {
    public UserDto getUser(long userId) {
        return new UserDto(userId, null, null);
    }

    public List<UserDto> getUsersByIds(List<Long> ids) {
        return new ArrayList<>();
    }

    public List<Long> getFollowers(Long id) {
        return new ArrayList<>();
    }

    public List<Long> getOnlyActiveUsersFromList(List<Long> ids) {
        return new ArrayList<>();
    }
}
