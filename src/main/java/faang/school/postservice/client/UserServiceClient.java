package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @Retryable
    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @Retryable
    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);
    @Retryable
    @GetMapping("/users/{userId}/exists")
    boolean existsById(@PathVariable long userId);

    @Retryable
    @GetMapping("/subscriptions/followers/{followeeId}")
    List<Long> getFollowersIds(@PathVariable long followeeId);

    @Retryable
    @GetMapping("/subscriptions/followings/{followerId}")
    List<Long> getFollowingIds(@PathVariable long followerId);
}