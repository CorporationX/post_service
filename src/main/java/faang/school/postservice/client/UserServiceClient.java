package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserClientResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service",
        url = "${user-service.host}:${user-service.port}",
        path = "/api/v1/users",
        configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/me")
    UserClientResponseDto getCurrentUser();

    @GetMapping("/{userId}")
    UserClientResponseDto getUserById(@PathVariable long userId);

    @GetMapping()
    List<UserClientResponseDto> getUsersByIds(@RequestParam List<Long> userIds);
}
