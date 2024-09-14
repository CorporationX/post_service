package faang.school.postservice.exception.handler;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<String> handleFeignException(FeignException exception) {
        String message = exception.getMessage();
        log.error(message);

        return ResponseEntity
                .status(exception.status())
                .body(message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        if (fieldError == null) {
            return ResponseEntity.badRequest().build();
        }

        String message = fieldError.getDefaultMessage();
        log.error(message);

        return ResponseEntity.badRequest().body(message);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
        String message = exception.getMessage();
        log.error(message);

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }
}
