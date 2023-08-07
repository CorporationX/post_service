package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users/list")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping(value = "/subscription/followers/{followeeId}", consumes = "application/json")
    List<UserDto> getFollowing(@PathVariable long followeeId);
}