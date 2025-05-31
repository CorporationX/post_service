package faang.school.postservice.controller.handler;

import faang.school.postservice.exception.AlbumAccessDeniedException;
import faang.school.postservice.exception.OutboxPublishException;
import faang.school.postservice.exception.RedisCacheException;
import faang.school.postservice.exception.RedisUnavailableException;
import io.lettuce.core.RedisConnectionException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AlbumAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAlbumAccessDeniedException(AlbumAccessDeniedException ex) {
        return createErrorResponse(HttpStatus.FORBIDDEN, ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return createErrorResponse(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        return createErrorResponse(HttpStatus.BAD_REQUEST, ex);
    }

    @ExceptionHandler(ExecutionException.class)
    public ResponseEntity<ErrorResponse> handleExecutionException(ExecutionException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(RedisUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleRedisUnavailableException(RedisUnavailableException ex) {
        return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex);
    }

    @ExceptionHandler(RedisCacheException.class)
    public ResponseEntity<ErrorResponse> handleRedisCacheException(RedisCacheException ex) {
        return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex);
    }

    @ExceptionHandler(RedisConnectionException.class)
    public ResponseEntity<ErrorResponse> handleRedisConnectionException(RedisConnectionException ex) {
        return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex);
    }

    @ExceptionHandler(OutboxPublishException.class)
    public ResponseEntity<ErrorResponse> handleOutboxPublishException(OutboxPublishException ex) {
        return createErrorResponse(HttpStatus.SERVICE_UNAVAILABLE, ex);
    }

    @ExceptionHandler(InterruptedException.class)
    public ResponseEntity<ErrorResponse> handleInterruptedException(InterruptedException ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex);
    }

    private ResponseEntity<ErrorResponse> createErrorResponse(HttpStatus status, Exception ex) {
        String message = ex.getMessage();
        log.error("Error: {}", message, ex);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now().toString())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message != null ? message : "Unexpected server error")
                .build();

        return ResponseEntity.status(status).body(errorResponse);
    }
}

