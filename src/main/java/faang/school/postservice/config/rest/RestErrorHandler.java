package faang.school.postservice.config.rest;

import faang.school.postservice.exception.ExternalServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

@Slf4j
public class RestErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String statusCode = response.getStatusCode().toString();
        String statusText = response.getStatusText();
        String errorMessage = String.format(
                "HTTP error from external service:%n- Status: %s%n- Reason: %s", statusCode, statusText);
        log.error(errorMessage);
        throw new ExternalServiceException(errorMessage);
    }
}
