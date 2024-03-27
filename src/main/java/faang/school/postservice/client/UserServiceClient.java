package faang.school.postservice.client;

import faang.school.postservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(path = "/api",name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping("/users/exists/{userId}")
    boolean isUserExists(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("/user/{id}/followersIds")
    List<Long> getSubscriberIdsByUserId(@PathVariable long id);

    @PostMapping("/user/{id}/followeesIds")
    List<Long> getSubscriptionsIdsByUserId(@PathVariable long id);
}
