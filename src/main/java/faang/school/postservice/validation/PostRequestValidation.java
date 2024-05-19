package faang.school.postservice.validation;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.zip.DataFormatException;

@ControllerAdvice
@Slf4j
public class PostRequestValidation {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
        log.error("EntityNotFoundException {}", exception.getMessage());
        return ResponseEntity.status(204).body(exception.getMessage());
    }

    @ExceptionHandler(DataFormatException.class)
    public ResponseEntity<String> handleDataValidationException(DataValidationException exception) {
        log.error("DataValidationException {}", exception.getMessage());
        return ResponseEntity.status(200).body(exception.getMessage());
    }
}
