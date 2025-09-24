package faang.school.postservice.client;

import faang.school.postservice.dto.user.follower.FollowersPage;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service", contextId = "followFeignClient", url = "${user-service.host}:${user-service.port}")
public interface FollowFeignClient {

    @GetMapping("/api/v1/follows/{authorId}/followers/ids")
    FollowersPage getFollowerIds(
            @PathVariable long authorId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "1000") int limit);

    @GetMapping("/api/v1/follows/{userId}/following/ids")
    FollowersPage getFollowingIds(
            @PathVariable long userId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "1000") int limit);
}
