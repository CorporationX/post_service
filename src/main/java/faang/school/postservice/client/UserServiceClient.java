package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/api/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/api/users/postId/{postId}")
    UserDto getUserByPostId(@PathVariable long postId);

    @GetMapping("/api/users/all/ids")
    List<Long> getAllUsersId();
}
