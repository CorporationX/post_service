package faang.school.postservice.client;

import faang.school.postservice.dto.feed.UserFeedHeatDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${services.user-service.host}:${services.user-service.port}", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/api/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/api/subscriptions/followers/{followeeId}")
    List<UserDto> getFollowers(@PathVariable long followeeId);

    @GetMapping("/api/v1/users/batch")
    List<UserFeedHeatDto> getUserIdsByBatch(@RequestParam int batchSize, @RequestParam long startingFromId);
}
