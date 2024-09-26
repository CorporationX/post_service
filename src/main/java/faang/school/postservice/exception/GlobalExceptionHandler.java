package faang.school.postservice.exception;

import faang.school.postservice.exception.comment.CommentNotFoundException;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
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
}
