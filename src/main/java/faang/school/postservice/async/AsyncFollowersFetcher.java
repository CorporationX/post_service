package faang.school.postservice.async;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AsyncFollowersFetcher {
    private final UserServiceClient userServiceClient;

    @Async
    public CompletableFuture<List<UserDto>> getFollowersAsync(Long followeeId) {
        List<UserDto> followers = userServiceClient.getFollowers(followeeId);
        return CompletableFuture.completedFuture(followers);
    }

    public List<UserDto> getFollowers(Long followeeId) {
        try {
            return getFollowersAsync(followeeId).get();
        } catch (InterruptedException e) {
            log.error("Interrupted while fetching followers of user with Id {}", followeeId, e);
        } catch (ExecutionException e) {
            log.error("Execution error when fetching followers for user with ID {}", followeeId, e);
        }
        return List.of();
    }


}
