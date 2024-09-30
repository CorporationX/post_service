package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${service.name}")
    private String serviceName;

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error("DataValidationException found and occurred: {}", e.getMessage(), e);
        String errorMessage = String.format("Data validation failed: %s", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(errorMessage)
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException occurred: {}", e.getMessage(), e);
        String errorMessage = String.format("Entity not found: %s", e.getMessage());
        return ErrorResponse.builder()
                .serviceName(serviceName)
                .globalMessage(errorMessage)
                .status(HttpStatus.NOT_FOUND.value())
                .build();
    }
}