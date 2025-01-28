package faang.school.postservice.client;

import faang.school.postservice.dto.UserDto;
import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.dto.user.UserFollowersDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{id}")
    UserDto getUserById(@PathVariable long id);

    @GetMapping("/api/v1/users/followers/{id}")
    UserFollowersDto getUserFollowersById(@PathVariable long id);

    @PostMapping("/api/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("/api/v1/users/subscriptions/{followeeId}")
    List<SubscriptionUserDto> getFollowers(@PathVariable("followeeId") Long followeeId);
}
