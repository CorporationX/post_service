package faang.school.postservice.handler;

import faang.school.postservice.dto.ErrorResponse;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.PostValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostValidationException.class)
    public ResponseEntity<ErrorResponse> handlePostValidationException(PostValidationException e) {
        return buildErrorResponse(e, HttpStatus.BAD_REQUEST, "PostValidationException");
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFoundException(PostNotFoundException e) {
        return buildErrorResponse(e, HttpStatus.NOT_FOUND, "PostNotFoundException");
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception e, HttpStatus StatusCode, String exceptionName) {
        log.error("{}: {}", exceptionName, e.getMessage(), e);
        ErrorResponse errorResponse = new ErrorResponse(
                e.getMessage(), StatusCode.value(), LocalDateTime.now());
        return ResponseEntity.status(StatusCode).body(errorResponse);
    }
}
