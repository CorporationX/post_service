package faang.school.postservice.client;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}", path = "api/v1/users")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable("userId") long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("/users/exists/{userId}")
    void existsUserById(@PathVariable("userId") long userId);

    @GetMapping("/{id}")
    UserDto getUser(@PathVariable("id") long Id);

    @GetMapping("/exists/{id}")
    boolean existsUserById(@PathVariable("id") long Id);
}