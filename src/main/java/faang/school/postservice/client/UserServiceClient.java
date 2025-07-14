package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.Min;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/api/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/api/v1/subscription/followers/{followeeId}")
    List<Long> getFollowers(@PathVariable Long followeeId);

    @GetMapping("/api/v1/subscription/followees/{followerId}")
    List<UserDto> getFollowees(@PathVariable Long followerId);

    @GetMapping("/api/v1/users/active")
    Page<UserDto> getActiveUsers(@RequestParam(defaultValue = "true") boolean active,
                                 @RequestParam @Min(0) int page,
                                 @RequestParam @Min(0) int size);
}
