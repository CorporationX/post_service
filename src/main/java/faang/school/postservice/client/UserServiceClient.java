package faang.school.postservice.client;

import faang.school.postservice.dto.page.PageDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {
    @GetMapping("/api/v1/users/{userId}")
    UserDto getUser(@PathVariable Long userId);

    @GetMapping("/api/v1/users/ids")
    List<UserDto> getUsersByIds(@RequestParam List<Long> ids);

    @GetMapping("/api/v1/users/{userId}/followers-ids")
    PageDto<Long> getUserFollowersIds(@PathVariable Long userId,
                                      @RequestParam int page,
                                      @RequestParam int size
    );
}
