package faang.school.postservice.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ErrorResponse errorResponse;

    @ExceptionHandler(DataValidationException.class)
    public ResponseEntity<ErrorResponse> handleDataValidationException(DataValidationException e) {
        loggingAndResponsePreparation(e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ErrorResponse> handleFeignException(FeignException e) {
        loggingAndResponsePreparation(e);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(Exception e) {
        loggingAndResponsePreparation(e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.info(e.getMessage(), e);
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fieldError -> Objects.requireNonNull(fieldError.getDefaultMessage(), "")))
                .entrySet().stream()
                .map(entry -> entry.getKey() + " : " + entry.getValue())
                .collect(Collectors.joining(", "));
        errorResponse = new ErrorResponse(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                errorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    private void loggingAndResponsePreparation(Exception e) {
        log.info(e.getMessage(), e);
        errorResponse = new ErrorResponse(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                e.getMessage());
    }
}
