package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Optional;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/api/v1/users/subscribers/{userId}")
    List<UserDto> getUserSubscribersDto(@PathVariable long userId);

    @GetMapping("/api/v1/users/subscribers/ids/{userId}")
    List<Long> getUserSubscribersIds(@PathVariable long userId);

    @GetMapping("/api/v1/projects/subscribers/{projectId}")
    List<Long> getProjectSubscriptions(@PathVariable long projectId);

    @GetMapping("/api/v1/users/all/ids")
    List<Long> getAllUserIds();

    @PostMapping("/api/v1/subscriptions/users/{followeeId}")
    void followUser(
            @PathVariable Long followeeId,
            @RequestHeader("x-user-id") Long followerId
    );

    @PostMapping("/api/v1/subscriptions/projects/{projectId}")
    void followProject(
            @PathVariable Long projectId,
            @RequestHeader("x-user-id") Long userId
    );

    @GetMapping("/api/v1/users/{userId}")
    Optional<UserDto> findById(@PathVariable("userId") Long userId);
    }