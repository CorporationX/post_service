package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service",
        url = "${user-service.host}:${user-service.port}",
        path = "${user-service.context-path}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId);

    @GetMapping("/users")
    List<UserDto> getUsersByIds(@RequestParam("ids") List<Long> ids);

    @GetMapping("/subscriptions/{followeeId}/followers")
    List<UserDto> getFollowers(
            @PathVariable long followeeId,
            @RequestParam(required = false) String namePattern,
            @RequestParam(required = false) String phonePattern,
            @RequestParam(defaultValue = "0") int experienceMin,
            @RequestParam(defaultValue = "2147483647") int experienceMax
    );

    @GetMapping("/subscriptions/{followerId}/followees")
    List<UserDto> getFollowees(@PathVariable long followerId,
                               @RequestParam(required = false) String namePattern,
                               @RequestParam(required = false) String phonePattern,
                               @RequestParam(defaultValue = "0") int experienceMin,
                               @RequestParam(defaultValue = "2147483647") int experienceMax);
}
