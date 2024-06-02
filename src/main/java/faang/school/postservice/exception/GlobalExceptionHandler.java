package faang.school.postservice.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataOperationException.class)
    public ResponseEntity<String> handlePostOperationException(DataOperationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
