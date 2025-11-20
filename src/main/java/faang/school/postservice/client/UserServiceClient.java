package faang.school.postservice.client;

import faang.school.postservice.dto.FolloweeSumDto;
import faang.school.postservice.dto.user.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "user-service", url = "${user-service.host}", configuration = FeignConfig.class)
public interface UserServiceClient {

    @GetMapping("/api/v1/users/{userId}")
    UserDto getUser(@PathVariable long userId);

    @PostMapping("/api/v1/users")
    List<UserDto> getUsersByIds(@RequestBody List<Long> ids);

    @GetMapping("/api/v1/subscriptions/countAuthors")
    int getAllAuthors();

    @GetMapping("/api/v1/subscriptions/countSubscribers")
    int getAllSubscribers();

    @GetMapping("/api/v1/subscriptions/authors")
    List<Integer> getAuthorsOrderdBySubscribers();

    @GetMapping("/api/v1/subscriptions/followers?followeeId={followeeId}")
    List<UserDto> getFollowers(@PathVariable long followeeId);
    
    @GetMapping("/api/v1/subscriptions/followeesPaged?page={page}&size={size}")
    List<FolloweeSumDto> getFolloweesPaged(@PathVariable int page, @PathVariable int size);

    @GetMapping("/api/v1/subscriptions/followersPaged?page={page}&size={size}")
    List<Long> getFollowersPaged(@PathVariable int page, @PathVariable int size);

    @GetMapping("/api/v1/subscriptions/following")
    List<UserDto> getFollowing(@RequestParam long followerId);
}
