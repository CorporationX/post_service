package faang.school.postservice.exception.handler;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.MethodArgumentNotValidException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e) {
        log.error("DataValidationException found and occurred: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .serviceName("PostService")
                .globalMessage("Validation error occurred")
                .fieldErrors(e.getFieldErrors())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("EntityNotFoundException occurred: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .serviceName("PostService")
                .globalMessage("Validation error occurred")
                .fieldErrors(e.getFieldErrors())
                .status(HttpStatus.NOT_FOUND.value())
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException occurred: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .serviceName("PostService")
                .globalMessage("Validation error occurred")
                .fieldErrors(e.getFieldErrors())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return ErrorResponse.builder()
                .serviceName("PostService")
                .globalMessage("Validation error occurred")
                .fieldErrors(e.getFieldErrors())
                .status(HttpStatus.BAD_REQUEST.value())
                .build();
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
//        log.error(exception.getMessage());
//        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
//    }

//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<String> handleEntityNotFoundException(EntityNotFoundException exception) {
//        log.error(exception.getMessage());
//        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
//    }

//    @ExceptionHandler(DataValidationException.class)
//    public ResponseEntity<String> handleDataValidationException(DataValidationException exception) {
//        log.error(exception.getMessage());
//        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
//    }
}