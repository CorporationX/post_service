package faang.school.postservice.client.handler;

import faang.school.postservice.exception.AuthorNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class UserNotFoundHandler implements FeignErrorHandler {
    @Override
    public boolean supports(String methodKey, int status) {
        return methodKey.contains("UserServiceClient#getUser") && status == 404;
    }

    @Override
    public Exception toException(String message) {
        return new AuthorNotFoundException(message);
    }
}
