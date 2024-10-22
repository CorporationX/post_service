package faang.school.postservice.exception.error_decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.FeignExceptionBody;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@RequiredArgsConstructor
@Component
public class UserServiceErrorDecoder implements ErrorDecoder {
    private final ObjectMapper objectMapper;

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String message = getFeignExceptionMessage(response);
        return new ExternalServiceException(
                status,
                "External service answered with exception : %s", message
        );
    }

    private String getFeignExceptionMessage(Response response) {
        String message;
        try (InputStream bodyIs = response.body().asInputStream()) {
            FeignExceptionBody feignExceptionBody = objectMapper.readValue(bodyIs, FeignExceptionBody.class);
            message = feignExceptionBody.getMessage();
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
        return message;
    }
}
