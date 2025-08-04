package faang.school.postservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class CustomErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default(); // Use default for unhandled cases

    @Override
    public Exception decode(String methodKey, Response response) {
        // Handle specific HTTP status codes
        switch (response.status()) {
            case 400:
                // Parse the response body for detailed error messages
                try {
                    var message = getErrorMessage(response.body(), "Bad Request.");
                    return new BadRequestException(message);
                } catch (Exception e) {
                    log.warn("error while parse error message:", e);
                    return new BadRequestException("Bad Request and failed to parse error details.");
                }
            case 404:
                try {
                    var message = getErrorMessage(response.body(), "Resource not found.");
                    return new NotFoundException(message);
                } catch (Exception e) {
                    log.warn("error while parse error message:", e);
                    return new NotFoundException("Resource not found.");
                }
            case 500:
                // Handle internal server errors
                return new InternalServerErrorException("Internal Server Error.");
            default:
                // Use the default error decoder for other cases
                return defaultErrorDecoder.decode(methodKey, response);
        }
    }

    private String getErrorMessage(Response.Body body, String defaultMessage) throws IOException {
        String bodyStr = body != null ?
                new String(body.asInputStream().readAllBytes())
                : null;
        // Example: If the body is JSON, parse it into a custom error object
        if (bodyStr != null && bodyStr.startsWith("{")) {
            ObjectMapper mapper = new ObjectMapper();
            // Assuming you have an ErrorDetails class
            // ErrorDetails errorDetails = mapper.readValue(body, ErrorDetails.class);
            var error = mapper.readValue(bodyStr, ErrorResponse.class);
            return error.getMessage();
        }
        return defaultMessage;
    }
}