package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserFilterDto;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping("/api/users/exists/{userId}")
    boolean isUserExists(@PathVariable long userId);

    @PostMapping("/api/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("api/user/{id}/followers")
    List<UserDto> getFollowers(@PathVariable("id") long followeeId, @RequestBody @Valid UserFilterDto filters);

    @PostMapping("api/user/{id}/followersIds")
    List<Long> getFollowerIds(@PathVariable("id") long followeeId);

    @GetMapping("/api/users/ids")
    List<Long> getUserIds();
}
