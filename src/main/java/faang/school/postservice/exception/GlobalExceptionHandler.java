package faang.school.postservice.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        log.warn("Validation failed: {}", errors, ex);
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Map<String, String>> handleDataValidation(DataValidationException ex) {
        log.warn("Data validation error", ex);
        Map<String, String> error = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        log.info("Entity not found: {}", ex.getMessage());
        Map<String, String> error = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException ex) {
        log.warn("Forbidden operation attempted", ex);
        Map<String, String> error = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });
        log.warn("Constraint violation: {}", errors, ex);
        return errors;
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalExceptions(RuntimeException ex) {
        log.warn("Illegal argument/state detected", ex);
        return Map.of("error", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAllUncaught(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return Map.of("error", "Internal server error");
    }

    @ExceptionHandler(AlreadyLikedException.class)
    public ResponseEntity<Map<String, String>> handleAlreadyLiked(AlreadyLikedException ex) {
        log.warn("User already liked entity", ex);
        Map<String, String> error = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLikeNotFound(LikeNotFoundException ex) {
        log.info("Like not found: {}", ex.getMessage());
        Map<String, String> error = Map.of("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}