package faang.school.postservice.client;

import faang.school.postservice.dto.filter.UserFilterDto;
import faang.school.postservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {

    @GetMapping("/api/user/{userId}")
    UserDto getUser(@PathVariable long userId);

    @GetMapping("/api/user")
    List<UserDto> getUsersByIds(@RequestParam List<Long> ids);

    @PutMapping("/api/user/{userId}/deactivate")
    UserDto deactivatesUserProfile(@PathVariable Long userId);

    @GetMapping("/api/user/premium")
    List<UserDto> getPremiumUsers(@RequestBody UserFilterDto userFilterDto);
}
