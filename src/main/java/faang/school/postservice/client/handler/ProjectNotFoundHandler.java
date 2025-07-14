package faang.school.postservice.client.handler;

import faang.school.postservice.exception.AuthorNotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(25)
public class ProjectNotFoundHandler implements FeignErrorHandler {
    @Override
    public boolean supports(String methodKey, int status) {
        return methodKey.contains("ProjectServiceClient#getProject") && status == 404;
    }

    @Override
    public Exception toException(String message) {
        return new AuthorNotFoundException(message);
    }
}
