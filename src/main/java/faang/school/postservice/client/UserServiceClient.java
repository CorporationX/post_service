package faang.school.postservice.client;

import faang.school.postservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    UserDto getUser(@PathVariable("userId") long userId);

    @PostMapping("/api/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("/api/v1/users/exists/{userId}")
    void existsUserById(@PathVariable("userId") long userId);

}
