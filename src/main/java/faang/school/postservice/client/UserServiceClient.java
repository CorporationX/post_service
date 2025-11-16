package faang.school.postservice.client;

import faang.school.postservice.dto.user.GetUsersDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service",
        url = "${user-service.host}:${user-service.port}${user-service.servlet.context-path}")
public interface UserServiceClient {

    @GetMapping("/users/{userId}")
    ResponseEntity<UserDto> getUser(@PathVariable long userId);

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody GetUsersDto getUsersDto);

    @PostMapping("/subscriptions/{followeeId}")
    List<UserDto> getFollowers(@PathVariable("followeeId") Long followeeId);

    @GetMapping("/users/not-banned")
    List<Long> getNotBannedUsersIds(@RequestParam List<Long> ids);
}