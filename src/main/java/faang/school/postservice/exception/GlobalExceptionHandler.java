package faang.school.postservice.exception;

import faang.school.postservice.dto.response.ErrorResponse;
import faang.school.postservice.dto.response.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            String message, HttpStatus status, String path, ErrorCode code) {

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .code(code)
                .build();
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePostNotFound(PostNotFoundException ex, HttpServletRequest request) {
        log.error("Post not found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, request.getRequestURI(), ErrorCode.POST_NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedPostAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedPostAccessException ex, HttpServletRequest request) {
        log.error("Unauthorized access: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.FORBIDDEN, request.getRequestURI(), ErrorCode.UNAUTHORIZED_ACCESS);
    }

    @ExceptionHandler(PostAlreadyPublishedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyPublished(PostAlreadyPublishedException ex, HttpServletRequest request) {
        log.error("Already published: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, request.getRequestURI(), ErrorCode.POST_ALREADY_PUBLISHED);
    }

    @ExceptionHandler(PostAlreadyDeletedException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyDeleted(PostAlreadyDeletedException ex, HttpServletRequest request) {
        log.error("Already deleted: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.GONE, request.getRequestURI(), ErrorCode.POST_ALREADY_DELETED);
    }

    @ExceptionHandler(InvalidPostScheduleException.class)
    public ResponseEntity<ErrorResponse> handleInvalidSchedule(InvalidPostScheduleException ex, HttpServletRequest request) {
        log.error("Invalid schedule: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), ErrorCode.INVALID_POST_SCHEDULE);
    }

    @ExceptionHandler(InvalidPostContentException.class)
    public ResponseEntity<ErrorResponse> handleInvalidContent(InvalidPostContentException ex, HttpServletRequest request) {
        log.error("Invalid content: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request.getRequestURI(), ErrorCode.INVALID_POST_CONTENT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        log.error("Validation failed: {}", errorMessage);
        return buildErrorResponse(errorMessage, HttpStatus.BAD_REQUEST, request.getRequestURI(), ErrorCode.VALIDATION_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error", ex);
        return buildErrorResponse("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), ErrorCode.INTERNAL_ERROR);
    }

    @ExceptionHandler(MissingUserContextException.class)
    public ResponseEntity<ErrorResponse> handleMissingUserContext(MissingUserContextException ex, HttpServletRequest request) {
        log.error("Missing user context: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, request.getRequestURI(), ErrorCode.UNAUTHORIZED_ACCESS);
    }
}
