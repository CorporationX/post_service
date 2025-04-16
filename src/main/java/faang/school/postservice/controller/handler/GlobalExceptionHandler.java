package faang.school.postservice.controller.handler;

import faang.school.postservice.exception.AlbumAccessDeniedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlbumAccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAlbumAccessDeniedException(AlbumAccessDeniedException ex) {
        return createErrorResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<Map<String, Object>> handleExecutionException(ExecutionException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<Map<String, Object>> handleInterruptedException(InterruptedException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, Exception exception) {
        String message = exception.getMessage();
        log.error("Error: {}", message, exception);
        return ResponseEntity.status(status).body(Map.of(
                "error", status.getReasonPhrase(),
                "message", message != null ? message : "Unknown error"
        ));
    }
}

