package faang.school.postservice.exception;

import faang.school.postservice.exception.post.PostOperationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(PostOperationException.class)
    public ResponseEntity<String> handlePostOperationException(PostOperationException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
