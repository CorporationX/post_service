package faang.school.postservice.controller;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.EntityWrongParameterException;
import faang.school.postservice.exception.NoAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse errorResponse = new ErrorResponse("Validation Error", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse("Entity Not Found", List.of(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityWrongParameterException.class)
    public ResponseEntity<ErrorResponse> handleEntityWrongParameterException(EntityWrongParameterException e) {
        ErrorResponse errorResponse = new ErrorResponse("Entity Wrong Parameter", List.of(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAccessException.class)
    public ResponseEntity<ErrorResponse> handleNoAccessException(NoAccessException e) {
        ErrorResponse errorResponse = new ErrorResponse("No Access", List.of(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}

