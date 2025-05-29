package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "subscription-service", url = "${user-service.host}:${user-service.port}")
public interface SubscriptionServiceClient {

    @GetMapping("/subscriptions/{followeeId}/followers/ids")
    List<Long> getFolloweeIds(@PathVariable("followeeId") long userId);

    @GetMapping("/subscriptions/{followeeId}/followers")
    List<UserDto> getFolloweeByUserId(@PathVariable("followeeId") long userId);
}
