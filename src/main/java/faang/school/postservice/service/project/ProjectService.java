package faang.school.postservice.service.project;

import faang.school.postservice.client.ProjectServiceClient;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectServiceClient projectServiceClient;

    @Retryable(
            retryFor = {SocketTimeoutException.class,
            ConnectException.class,
            FeignException.ServiceUnavailable.class,
            FeignException.GatewayTimeout.class},
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void checkProjectExist(Long userId) {
        projectServiceClient.checkProjectExists(userId);
    }
}
