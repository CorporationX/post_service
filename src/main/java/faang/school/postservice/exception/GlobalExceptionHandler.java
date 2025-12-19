package faang.school.postservice.exception;

import faang.school.postservice.dto.ExceptionDto;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDto> handleResourceNotFound(EntityNotFoundException e) {
        String description = "Resource not found";
        log.error(description, e);
        return buildExceptionResponse(HttpStatus.NOT_FOUND, description, e.getMessage());
    }

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ExceptionDto> handleResourceBadRequest(DataValidationException e) {
        String description = "Incorrect data has been entered";
        log.error(description, e);
        return buildExceptionResponse(HttpStatus.BAD_REQUEST, description, e.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ExceptionDto> handleResourceBadRequest(ForbiddenException e) {
        String description = "Forbidden action";
        log.error(description, e);
        return buildExceptionResponse(HttpStatus.FORBIDDEN, description, e.getMessage());
    }

    @ExceptionHandler(FileException.class)
    public ResponseEntity<ExceptionDto> handleFileException(FileException e) {
        String description = "File not found or incorrect";
        log.error(description, e);
        return buildExceptionResponse(HttpStatus.BAD_REQUEST, description, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String description = "Validation failed";
        log.error(description, ex);
        Map<String, String> errors = extractValidationErrors(ex);

        String errorMessage = errors.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining("; "));

        return buildExceptionResponse(HttpStatus.BAD_REQUEST, description, errorMessage);
    }

    private Map<String, String> extractValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ExceptionDto> handleFeignNotFoundException(FeignException.NotFound e) {
        String description = "Remote resource not found";
        log.error(description, e);
        return buildExceptionResponse(HttpStatus.NOT_FOUND, description, e.getMessage());
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ExceptionDto> handleFeignException(FeignException e) {
        String description = "Service unavailable";
        log.error(description, e);
        return buildExceptionResponse((HttpStatus.SERVICE_UNAVAILABLE), description, e.getMessage());
    }

    @ExceptionHandler(FeignException.ServiceUnavailable.class)
    public ResponseEntity<ExceptionDto> handleFeignServiceUnavailable(FeignException.ServiceUnavailable e) {
        String description = "Service is currently unavailable. Please try again later.";
        log.error(description, e);
        return buildExceptionResponse(HttpStatus.SERVICE_UNAVAILABLE, description, e.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleResourceNotFoundException(ResourceNotFoundException exception) {
        return new ErrorResponse(LocalDateTime.now(), exception.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException exception) {
        return new ErrorResponse(LocalDateTime.now(), exception.getMessage());
    }

    private ResponseEntity<ExceptionDto> buildExceptionResponse(HttpStatus status,
                                                                String description,
                                                                String errorMessage) {
        return ResponseEntity.status(status).body(
                new ExceptionDto(
                        status.value(),
                        description,
                        errorMessage
                ));
    }
}