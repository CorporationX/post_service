package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}")
public interface UserServiceClient {


    @GetMapping("/api/v1/user/{userId}")
    UserDto getUserById(@PathVariable long userId);

    @PostMapping("/api/v1/user/byList")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @PutMapping("/api/v1/user/avatar/{fileId}/{smallFileId}")
    void uploadAvatar(@RequestHeader(value = "x-user-id") long userId,
                             @PathVariable String fileId,
                             @PathVariable String smallFileId);

    @DeleteMapping("/api/v1/user/avatar/delete")
    void deleteAvatar(@RequestHeader(value = "x-user-id") long userId);
}
