package faang.school.postservice.client;

import faang.school.postservice.dto.user.UserDto;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeignUserServiceAdapter {
    private final UserServiceClient userFeignClient;

    @Retryable(retryFor = {FeignException.class, SocketTimeoutException.class},
            maxAttemptsExpression = "${feign.retry.maxAttempts}",
            backoff = @Backoff(
                    delayExpression = "${feign.retry.delay}",
                    multiplierExpression = "${feign.retry.multiplier}"
            ))
    public List<UserDto> fetchUserDtosViaFeign(List<Long> idsToFetch,
                                               String entityNameForLog,
                                               Long entityIdForLog) {
        if (idsToFetch == null || idsToFetch.isEmpty()) {
            log.info("Skipping user fetch via FeignClient for entity '{}', " +
                            "ID {} as no user IDs were provided to fetch.",
                    entityNameForLog, entityIdForLog);
            return Collections.emptyList();
        }
        return userFeignClient.getUsersByIds(idsToFetch);
    }

    @Recover
    private List<UserDto> recoverFetchUserDtos(FeignException e,
                                               List<Long> idsToFetch,
                                               String entityNameForLog,
                                               Long entityIdForLog) {
        log.error("All retry attempts failed for FeignClient call (FeignException) for entity '{}', ID {}. " +
                        "Requested User IDs: {}. Status: {}, Response: '{}'. Error: {}",
                entityNameForLog,
                entityIdForLog,
                idsToFetch,
                e.status(),
                e.contentUTF8(),
                e.getMessage(),
                e);
        return Collections.emptyList();
    }

    @Recover
    private List<UserDto> recoverFetchUserDtos(SocketTimeoutException e,
                                               List<Long> idsToFetch,
                                               String entityNameForLog,
                                               Long entityIdForLog) {
        log.error("All retry attempts failed for FeignClient call (SocketTimeoutException) for entity '{}', ID {}. " +
                        "Requested User IDs: {}. Error: {}",
                entityNameForLog,
                entityIdForLog,
                idsToFetch,
                e.getMessage(),
                e);
        return Collections.emptyList();
    }
}
