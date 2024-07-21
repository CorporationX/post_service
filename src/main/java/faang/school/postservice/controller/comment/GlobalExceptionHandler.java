package faang.school.postservice.controller.comment;

import faang.school.postservice.exception.EntityWrongParameterException;
import faang.school.postservice.exception.NoAccessException;
import jakarta.persistence.EntityNotFoundException;
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
    public ResponseEntity<faang.school.postservice.handler.ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        faang.school.postservice.handler.ErrorResponse errorResponse = new faang.school.postservice.handler.ErrorResponse("Validation Error", errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<faang.school.postservice.handler.ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        faang.school.postservice.handler.ErrorResponse errorResponse = new faang.school.postservice.handler.ErrorResponse("Entity Not Found", List.of(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityWrongParameterException.class)
    public ResponseEntity<faang.school.postservice.handler.ErrorResponse> handleEntityWrongParameterException(EntityWrongParameterException e) {
        faang.school.postservice.handler.ErrorResponse errorResponse = new faang.school.postservice.handler.ErrorResponse("Entity Wrong Parameter", List.of(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoAccessException.class)
    public ResponseEntity<faang.school.postservice.handler.ErrorResponse> handleNoAccessException(NoAccessException e) {
        faang.school.postservice.handler.ErrorResponse errorResponse = new faang.school.postservice.handler.ErrorResponse("No Access", List.of(e.getMessage()));
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
}

