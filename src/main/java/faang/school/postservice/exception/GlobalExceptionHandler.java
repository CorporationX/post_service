package faang.school.postservice.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import faang.school.postservice.constant.ValidationConstant;
import faang.school.postservice.dto.error.InvalidFormatResponseDto;
import faang.school.postservice.exception.comment.CommentNotFoundException;
import faang.school.postservice.exception.like.LikeNotFoundException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleCommentNotFoundException(ValidationException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException exception) {
        Map<String, Object> body = new HashMap<>();
        body.put("message", exception.getMessage());

        return new ResponseEntity<>(body, exception.getHttpStatus());
    }

    @ExceptionHandler(InputValidationException.class)
    public ResponseEntity<?> handleInputValidationErrors(InputValidationException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .errorFields(ex.getErrorFields())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<?> handleCommentNotFoundException(CommentNotFoundException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<?> handleExternalException(ExternalServiceException ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build(),
                ex.getStatus()
        );
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<?> handleFeignServiceUnavailableException(FeignException ex) {
        String message;

        if (ex.getCause() instanceof ConnectException) {
            message = "Connection to external service failed";
        } else {
            message = ex.getMessage();
        }
        log.error("FeignException occurred: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(message)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(ex.getMessage())
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(LikeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleLikeNotFoundException(LikeNotFoundException e) {
        log.error("LikeNotFoundException occurred: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(RecordAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleRecordAlreadyExistsException(RecordAlreadyExistsException e) {
        log.error("RecordAlreadyExistsException occurred: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.error("UserNotFoundException occurred: {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(InvalidFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public InvalidFormatResponseDto handleInvalidFormatException(InvalidFormatException e) {
        log.error("InvalidFormatException occurred: {}", e.getMessage(), e);
        String fieldName = e.getPath().get(0).getFieldName();
        return new InvalidFormatResponseDto(ValidationConstant.INVALID_FORMAT, fieldName, getExpectedFormat(fieldName));
    }

    private String getExpectedFormat(String fieldName) {
        return switch (fieldName) {
            case "createdAt", "updatedAt" -> ValidationConstant.DATE_FORMAT;
            default -> {
                log.warn("Unexpected field name: {}", fieldName);
                yield ValidationConstant.UNKNOWN_FORMAT;
            }
        };
    }
}
