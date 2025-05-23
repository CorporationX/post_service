package faang.school.postservice.service.user;

import faang.school.postservice.client.UserServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserServiceClient userServiceClient;

    @Retryable(
            retryFor = {SocketTimeoutException.class,
                    ConnectException.class,
                    FeignException.ServiceUnavailable.class,
                    FeignException.GatewayTimeout.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void checkUserExist(Long userId) {
        userServiceClient.checkUserExists(userId);
    }
}
