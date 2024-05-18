package faang.school.postservice.exception;

import faang.school.postservice.exception.like.LikeOperatingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(LikeOperatingException.class)
    public ResponseEntity<String> handleLikeOperatingException(LikeOperatingException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
