package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserProfilePicDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}", path = "/api/v1/user")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/byIds")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PutMapping("/avatar/put")
    void uploadAvatar(@RequestHeader(value = "x-user-id") long userId,
                             @RequestBody UserProfilePicDto userProfilePicDto);

    @DeleteMapping("/avatar/delete")
    void deleteAvatar(@RequestHeader(value = "x-user-id") long userId);

    @GetMapping("avatar/keys")
    UserProfilePicDto getAvatarKeys(@RequestHeader(value = "x-user-id") long userId);

    @GetMapping("/users/exists/{userId}")
    boolean existsById(@PathVariable Long userId);

    @GetMapping("/users/{userId}/followers")
    List<UserDto> getUserFollowers(@PathVariable long userId);

    @GetMapping("/users/{userId}/authors")
    List<UserDto> getUserSubscribedAuthors(@PathVariable long userId);

    @PostMapping("/users/exists/followers")
    boolean doesFollowersExist(@RequestBody List<Long> followerIds);
}
