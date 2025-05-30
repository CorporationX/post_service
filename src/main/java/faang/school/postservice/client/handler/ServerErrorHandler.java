package faang.school.postservice.client.handler;

import faang.school.postservice.exception.ExternalServiceException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(75)
public class ServerErrorHandler implements FeignErrorHandler {
    @Override
    public boolean supports(String methodKey, int status) {
        return status >= 500;
    }

    @Override
    public Exception toException(String message) {
        return new ExternalServiceException("Server error: " + message);
    }
}

