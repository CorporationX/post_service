package faang.school.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "user-service", contextId = "subscriptionServiceClient",
        url = "${user-service.host}:${user-service.port}")
public interface SubscriptionServiceClient {

    @GetMapping("/users/subscription/followers/{followeeId}/count")
    Long getFollowersCount(@PathVariable("followeeId") long userId);

    @GetMapping("/users/subscription/followers/{followeeId}/ids")
    List<Long> getFollowerIds(@PathVariable("followeeId") long userId);
}