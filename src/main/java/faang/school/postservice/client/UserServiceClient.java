package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/api")
public interface UserServiceClient {

    @PostMapping("/users/heat")
    void getAllUsersAndFolowees();

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping("/users/follower/{userId}")
    List<Long> getFollowerIds(@PathVariable long userId);

    @PostMapping("/users/")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);
}
