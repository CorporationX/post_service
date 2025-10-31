package faang.school.postservice.controller.exception.comment;

import faang.school.postservice.exeption.ResourceNotFoundException;
import faang.school.postservice.exeption.UserInactiveException;
import faang.school.postservice.exeption.UserNotFoundException;
import faang.school.postservice.exeption.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Class<? extends Exception>, ErrorInfo> EXCEPTION_MAPPINGS = Map.of(
            ResourceNotFoundException.class, new ErrorInfo(HttpStatus.NOT_FOUND, "Resource Not Found"),
            UserInactiveException.class, new ErrorInfo(HttpStatus.FORBIDDEN, "User Inactive"),
            ValidationException.class, new ErrorInfo(HttpStatus.BAD_REQUEST, "Validation Error"),
            UserNotFoundException.class, new ErrorInfo(HttpStatus.NOT_FOUND, "User Not Found")
    );

    private record ErrorInfo(HttpStatus status, String errorMessage) {}

    private ResponseEntity<Map<String, Object>> buildErrorResponse(
            HttpStatus status, String error, String message) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message);
        body.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        ErrorInfo errorInfo = EXCEPTION_MAPPINGS.get(ex.getClass());

        if (errorInfo == null) {
            log.error("Unexpected error: ", ex);
            errorInfo = new ErrorInfo(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        } else if (errorInfo.status.is4xxClientError()) {
            log.warn("{}: {}", errorInfo.errorMessage, ex.getMessage());
        } else {
            log.error("{}: {}", errorInfo.errorMessage, ex.getMessage());
        }

        String message = ex.getMessage();

        if (ex instanceof MethodArgumentNotValidException manvEx) {
            message = manvEx.getBindingResult().getFieldErrors().stream()
                    .map(err -> err.getField() + ": " + err.getDefaultMessage())
                    .collect(Collectors.joining("; "));
        }

        return buildErrorResponse(errorInfo.status, errorInfo.errorMessage, message);
    }
}


