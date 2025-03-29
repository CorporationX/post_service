package faang.school.postservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        log.error("Validation failed: {}", errors);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        String message = ex.getMessage();
        log.error("Bad request: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(DataAlreadyDeletedException.class)
    public ResponseEntity<Object> handleDataAlreadyDeletedException(DataAlreadyDeletedException ex) {
        String message = ex.getMessage();
        log.error("Data already deleted: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(DataAlreadyExistException.class)
    public ResponseEntity<Object> handleDataAlreadyExistException(DataAlreadyExistException ex) {
        String message = ex.getMessage();
        log.error("Data already exist: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(DataUpdateException.class)
    public ResponseEntity<Object> handleDataUpdateException(DataUpdateException ex) {
        String message = ex.getMessage();
        log.error("Data update: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        String message = ex.getMessage();
        log.error("Entity not found: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(RequiredOwnerException.class)
    public ResponseEntity<Object> handleRequiredOwnerException(RequiredOwnerException ex) {
        String message = ex.getMessage();
        log.error("Required owner: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(SinglePostAuthorException.class)
    public ResponseEntity<Object> handleSinglePostAuthorException(SinglePostAuthorException ex) {
        String message = ex.getMessage();
        log.error("Single post author: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(UnpublishedPostException.class)
    public ResponseEntity<Object> handleUnpublishedPostException(UnpublishedPostException ex) {
        String message = ex.getMessage();
        log.error("Unpublished post: {}", message, ex);
        return buildErrorResponseEntity(HttpStatus.BAD_REQUEST, message, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        log.error("Internal server error: {}", ex.getMessage(), ex);
        return buildErrorResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", null);
    }

    private ResponseEntity<Object> buildErrorResponseEntity(
            HttpStatus status, String message, Map<String, String> errors) {
        ErrorResponse apiError = new ErrorResponse(status.value(), message, errors);
        return ResponseEntity.status(status).body(apiError);
    }
}
