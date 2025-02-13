package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserCacheDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserFeignService {
    private final UserServiceClient userServiceClient;

    @Retryable(retryFor = Exception.class,
            maxAttemptsExpression = "@retryProperties.maxAttempts",
            backoff = @Backoff(
                    delayExpression = "@retryProperties.initialDelay",
                    multiplierExpression = "@retryProperties.multiplier",
                    maxDelayExpression = "@retryProperties.maxDelay"
            )
    )
    public UserCacheDto getCacheUser (Long userId) {
        try {
            return userServiceClient.getCacheUser(userId);
        } catch (FeignException e) {
            log.error("Error occurred while fetching  for user {}", userId, e);
            throw e;
        }
    }
}
