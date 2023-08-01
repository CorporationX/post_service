package faang.school.postservice.exceptionHandler;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotAllowedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<String> handleNotFoundException(JsonProcessingException ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<String> handleNotFoundException(NotAllowedException ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception ex) {
        log.error(ex.getMessage(), ex.getCause());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
