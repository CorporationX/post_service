package faang.school.postservice.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationErrors(MethodArgumentNotValidException exception) {
        log.error("Validation Error:", exception);
        return exception.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        e -> e.getField(),
                        e -> e.getDefaultMessage(),
                        (msg1, msg2) -> msg1
                ));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolations(ConstraintViolationException exception) {
        log.error("Constraint Violation:", exception);
        return exception.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString().replaceAll(".*\\.", ""),
                        ConstraintViolation::getMessage
                ));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Map<String, String> handleNotReadable(HttpMessageNotReadableException exception) {
        log.error("Unreadable JSON:", exception);
        return Map.of("error", "Unreadable JSON request");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Map<String, String> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String name = exception.getName();
        String required = exception.getRequiredType() != null ? exception.getRequiredType().getSimpleName() : "unknown";
        log.error("Type Mismatch:", exception);
        return Map.of(name, "must be of type " + required);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Map<String, String> handleMissingParam(MissingServletRequestParameterException exception) {
        log.error("Missing Param:", exception);
        return Map.of(exception.getParameterName(), "is required");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public Map<String, String> handleNotFound(EntityNotFoundException exception) {
        log.error("Not found:", exception);
        return Map.of("error", exception.getMessage() != null ? exception.getMessage() : "Not found");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public Map<String, String> handleForbidden(ForbiddenException exception) {
        log.error("Forbidden:", exception);
        return Map.of("error", exception.getMessage() != null ? exception.getMessage() : "Forbidden");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataValidationException.class)
    public Map<String, String> handleDataValidation(DataValidationException exception) {
        log.error("Validation error:", exception);
        return Map.of("error", exception.getMessage() != null ? exception.getMessage() : "Validation error");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String, String> handleIllegalArgument(IllegalArgumentException exception) {
        log.error("Bad request:", exception);
        return Map.of("error", exception.getMessage() != null ? exception.getMessage() : "Bad request");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, String> handleUnexpected(Exception exception) {
        log.error("Unexpected error:", exception);
        return Map.of("error", "Internal server error");
    }
}
