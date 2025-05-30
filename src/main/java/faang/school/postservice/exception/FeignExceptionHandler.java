package faang.school.postservice.exception;

import faang.school.postservice.exception.errors.ErrorRule;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeignExceptionHandler {

    private final List<ErrorRule> errorRules = List.of(
            new ErrorRule(
                    status -> status == 404,
                    () -> new UserNotFoundException("User not found")
            ),
            new ErrorRule(
                    status -> status >= 400 && status < 500,
                    () -> new DataValidationException("Client error from user service")
            ),
            new ErrorRule(
                    status -> status >= 500,
                    () -> new UserServiceUnavailableException("User service is currently unavailable. Please try later.")
            ),
            new ErrorRule(
                    status -> status < 0,
                    () -> new UserServiceUnavailableException("User service is unreachable. Connection refused or network error.")
            )
    );

    public void handleFeignException(FeignException e, Long authorId) {
        int status = e.status();
        log.error("Error from user_service. Status: {}, Message: {}", status, e.getMessage());

        Supplier<RuntimeException> exceptionSupplier = errorRules.stream()
                .filter(rule -> rule.getCondition().test(status))
                .map(ErrorRule::getExceptionSupplier)
                .findFirst()
                .orElse(() -> new RuntimeException("Unexpected error from user_service: " + e.getMessage()));

        // Для 404 добавляем ID в сообщение
        if (status == 404) {
            throw new UserNotFoundException("User with ID: %d not found".formatted(authorId));
        }

        throw exceptionSupplier.get();
    }
}

