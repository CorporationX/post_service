package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserFilterDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/api/v1")
@ConditionalOnProperty(name = "mock.user-service.client.enabled", havingValue = "false")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/user/{id}/exists")
    boolean existsUserById(@PathVariable long id);

    @GetMapping("/subscription/followers")
    List<UserDto> getFollowers(@RequestParam Long followeeId, @RequestBody UserFilterDto userFilterDto);

    @GetMapping("following")
    List<UserDto> getFollowing(@RequestParam Long followeeId, @RequestBody UserFilterDto filter);

    default List<Long> getFollowingIds(Long followeeId) {
        List<UserDto> followers = getFollowing(followeeId, new UserFilterDto());
        return followers.stream()
                .map(UserDto::getId)
                .toList();
    }
}
