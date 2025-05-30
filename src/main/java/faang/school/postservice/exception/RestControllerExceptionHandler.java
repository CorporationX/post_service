package faang.school.postservice.exception;

import faang.school.postservice.exception.errors.ApiError;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class RestControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.error("Validation error: {}", errorMsg);
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ApiError> handleDataValidationException(DataValidationException ex) {
        List<ExceptionMapping> mappings = List.of(
                new ExceptionMapping(UserNotFoundException.class, HttpStatus.NOT_FOUND),
                new ExceptionMapping(UserServiceUnavailableException.class, HttpStatus.SERVICE_UNAVAILABLE),
                new ExceptionMapping(DataValidationException.class, HttpStatus.BAD_REQUEST)
        );

        HttpStatus status = mappings.stream()
                .filter(mapping -> mapping.getExceptionClass().isInstance(ex))
                .findFirst()
                .map(ExceptionMapping::getStatus)
                .orElse(HttpStatus.INTERNAL_SERVER_ERROR);

        ApiError error = new ApiError(status, ex.getMessage());
        return new ResponseEntity<>(error, status);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.error("Malformed JSON request: {}", ex.getMessage());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST, "Malformed JSON request");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error("Entity not found: {}", ex.getMessage());
        ApiError error = new ApiError(HttpStatus.NOT_FOUND, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllUncaughtException(Exception ex) {
        log.error("Unexpected error: ", ex);
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
