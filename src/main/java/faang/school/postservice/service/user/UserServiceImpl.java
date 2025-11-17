package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserServiceClient userServiceClient;

    @Override
    @Retryable(retryFor = {FeignException.InternalServerError.class, FeignException.ServiceUnavailable.class},
            maxAttemptsExpression = "${user-service.retryable.maxAttempts}",
            backoff = @Backoff(delayExpression = "${user-service.retryable.delay}",
                    multiplierExpression = "${user-service.retryable.multiplier}"))
    public List<Long> getNotBannedUsersIds(List<Long> ids) {
        return userServiceClient.getNotBannedUsersIds(ids);
    }
}