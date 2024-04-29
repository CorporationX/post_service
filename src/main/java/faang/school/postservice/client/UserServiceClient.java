package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/api/v1")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/users/exists/{userId}")
    boolean existById (@PathVariable @Positive(message = "Id must be greater than zero") long userId);

    @GetMapping("/followers/{followeeId}")
    List<Long> getFollowersId(@RequestBody Long followeeId);

    @GetMapping("/activeUsers")
    List<Long> getActiveUsers();
}
