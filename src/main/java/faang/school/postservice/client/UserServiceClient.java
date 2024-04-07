package faang.school.postservice.client;

import faang.school.postservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}", path = "api/v1/users")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable("userId") long userId);

    @PostMapping
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/exists/{id}")
    boolean existsUserById(@PathVariable("id") long Id);

    @GetMapping("/{userId}/followersIds")
    List<Long> getFollowersIds(@PathVariable("userId") long userId);
}