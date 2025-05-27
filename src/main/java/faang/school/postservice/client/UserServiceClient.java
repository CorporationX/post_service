package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/subscriptions/{followeeId}/followers")
    List<UserDto> getFollowersByUserId(@PathVariable("followeeId") long userId);

    @GetMapping("/subscriptions/{followeeId}/followers/ids")
    List<Long> getFollowerIds(@PathVariable("followeeId") long userId);

    @GetMapping("/users")
    List<UserDto> getUsersByPage(
            @RequestParam("page") int page,
            @RequestParam("size") int size);
}
