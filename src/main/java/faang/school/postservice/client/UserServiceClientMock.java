package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserServiceClientMock implements UserServiceClient {

    @Override
    public UserDto getUser(long userId) {
        return new UserDto(userId, "MockUser" + userId, "mockemail" + userId + "@example.com");
    }

    @Override
    public List<UserDto> getUsersByIds(List<Long> ids) {
        return ids.stream()
                .map(id -> new UserDto(id, "MockUser" + id, "mockemail" + id + "@example.com"))
                .toList();
    }

    @Override
    public boolean existsUserById(long id) {
        return id % 2 == 0;
    }

    @Override
    public List<UserDto> getFollowers(Long followeeId, UserFilterDto userFilterDto) {
        return List.of(
                new UserDto(1L, "Follower1", "follower1@example.com"),
                new UserDto(2L, "Follower2", "follower2@example.com")
        );
    }

    @Override
    public List<UserDto> getFollowing(Long followeeId, UserFilterDto filter) {
        return List.of(
                new UserDto(1L, "Follower1", "follower1@example.com"),
                new UserDto(2L, "Follower2", "follower2@example.com")
        );
    }
}
