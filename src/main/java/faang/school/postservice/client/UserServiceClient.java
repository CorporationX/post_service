package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}", path = "api/v1/users")
public interface UserServiceClient {
    @GetMapping("/{id}")
    UserDto getUser(@PathVariable("id") long Id);

    @GetMapping("/exists/{id}")
    boolean existsUserById(@PathVariable("id") long Id);
}