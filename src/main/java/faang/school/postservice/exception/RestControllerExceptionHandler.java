package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public final ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("Validation error: {}", errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException exception) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", exception.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable() {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.BAD_REQUEST.value(),
                "error", "Bad Request",
                "message", "Malformed JSON request"
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
