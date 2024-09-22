package faang.school.postservice.exception.error_decoder;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.FeignExceptionBody;
import faang.school.postservice.exception.UnknownException;
import faang.school.postservice.exception.ValidationException;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class UserServiceErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String message;
        try (InputStream bodyIs = response.body().asInputStream()) {
            ObjectMapper objectMapper = new ObjectMapper();
            FeignExceptionBody feignExceptionBody = objectMapper.readValue(bodyIs, FeignExceptionBody.class);
            message = feignExceptionBody.getMessage();
        } catch (IOException exception) {
            throw new RuntimeException(exception.getMessage());
        }
        if (status.is4xxClientError()) {
            return new ValidationException(message);
        } else if (status.is5xxServerError()) {
            return new ExternalServiceException(message);
        }
        return new UnknownException(message);
    }
}
