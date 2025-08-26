package faang.school.postservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "follow-service",
        url = "${follow-service.host:http://localhost:8080}",
        path = "/api/v1/follow",
        configuration = FeignConfig.class
)
public interface FollowServiceClient {

    @GetMapping("/users")
    List<Long> getAllUserIds();

    @GetMapping("/{userId}/followees")
    List<Long> getFolloweeIds(@PathVariable("userId") Long userId);
}
