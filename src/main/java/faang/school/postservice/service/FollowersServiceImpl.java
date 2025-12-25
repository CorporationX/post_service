package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.externalservice.ExternalServiceException;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowersServiceImpl implements FollowersService {
    private final UserServiceClient userServiceClient;

    @Retryable(
            retryFor = FeignException.class,
            backoff = @Backoff(delay = 500, multiplier = 2.0)
    )
    public List<Long> getFollowerIds(Long authorId) {
        if (authorId == null) {
            log.warn("AuthorId is null, can't find followers");
            return Collections.emptyList();
        }

        try {
            List<UserDto> followers = userServiceClient.getFollowers(
                    authorId,
                    null,
                    null,
                    0,
                    Integer.MAX_VALUE
            );
            return followers.stream()
                    .map(UserDto::id)
                    .collect(Collectors.toList());
        } catch (FeignException.NotFound e) {
            log.warn("User {} not found when getting followers", authorId);
            return Collections.emptyList();
        } catch (FeignException e) {
            log.error("Failed to get followers from user-service for author {}", authorId, e);
            throw new ExternalServiceException("user_service", "Cannot fetch followers - user-service unavailable", e);
        } catch (Exception e) {
            log.error("Unexpected error when searching for followers for an author {}", authorId, e);
            throw new ExternalServiceException("user_service", "Cannot fetch followers - unexpected error", e);
        }
    }
}