package faang.school.postservice.client;

import faang.school.postservice.dto.filter.UserFilterDto;
import faang.school.postservice.dto.user.UserDto;
import jakarta.validation.constraints.Positive;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable(name = "userId") long userId);

    @GetMapping("/users")
    List<UserDto> getAllUsers();

    @PostMapping("/users/{userId}/subscriptions")
    List<UserDto> getFollowers(@PathVariable("userId") @Positive long followeeId, @RequestBody(required = false) UserFilterDto filter);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);


}
