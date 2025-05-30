package faang.school.postservice.client.handler;

import faang.school.postservice.exception.DataValidationException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(50)
public class ClientErrorHandler implements FeignErrorHandler {
    @Override
    public boolean supports(String methodKey, int status) {
        return status >= 400 && status < 500;
    }

    @Override
    public Exception toException(String message) {
        return new DataValidationException("Client error: " + message);
    }
}

