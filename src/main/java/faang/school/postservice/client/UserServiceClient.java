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
}
