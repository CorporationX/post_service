package faang.school.postservice.advice;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<String>> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> error = e.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        log.error("Data validation exception occurred: {}", e.getAllErrors());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<List<String>> constraintViolationException(ConstraintViolationException e) {
        List<String> error = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage).toList();
        log.error("Data validation exception occurred: {}", e.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> entityNotFoundException(EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
