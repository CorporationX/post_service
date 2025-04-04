package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PostMapping("/api/v1/users/list-by-ids")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PostMapping("/api/v1/users/page")
    Page<UserDto> getUsersByIds(
            @RequestParam("ids") List<Long> ids,
            Pageable pageable
    );

    @GetMapping("/api/v1/users/all")
    List<UserDto> getAllUsers();
}