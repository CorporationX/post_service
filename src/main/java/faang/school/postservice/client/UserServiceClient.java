package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/api/v1/user")
public interface UserServiceClient {

    @GetMapping("/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);
}
