package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    public final ResponseEntity<Object> handleDataValidationException(DataValidationException exception) {
        log.warn("Validation error: {}", exception.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(exception.getMessage());
    }
}
