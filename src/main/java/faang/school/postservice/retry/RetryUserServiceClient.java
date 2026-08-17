package faang.school.postservice.retry;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Класс для реализации повторных вызовов (retry)
 *
 * @author Linempy
 * @since 15.11.2025
 */
@Component
@RequiredArgsConstructor
public class RetryUserServiceClient {

    private final UserServiceClient client;
    private final UserContext userContext;

    @Retryable
    public Optional<UserViewDto> getUser(Long id) {
        userContext.setUserId(id);
        UserViewDto response = client.getUser(id);
        return Optional.ofNullable(response);
    }
}