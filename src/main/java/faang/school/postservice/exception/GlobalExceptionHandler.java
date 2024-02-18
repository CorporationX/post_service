package faang.school.postservice.exception;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final int INDEX_FIELD_ERRORS = 1;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("Exception Java Bean Validation API", ex);
        return ResponseEntity.badRequest().body(ex.getDetailMessageArguments()[INDEX_FIELD_ERRORS]);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<Object> handleDataValidationException(DataValidationException ex) {
        log.error("DataValidation Exception", ex);
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Object> handleValidationEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity Not Found Exception", ex);
        return ResponseEntity.of(Optional.of(ex.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleValidationRuntimeException(RuntimeException ex) {
        log.error("Runtime Exception", ex);
        return ResponseEntity.internalServerError().body(ex.getMessage());
    }
}