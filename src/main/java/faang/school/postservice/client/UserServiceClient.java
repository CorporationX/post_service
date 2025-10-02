package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserViewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserViewDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserViewDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/subscriptions/{followeeId}")
    ResponseEntity<List<Long>> getFollowerIds(@PathVariable Long followeeId);
}
