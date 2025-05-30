package faang.school.postservice.exception;

import faang.school.postservice.dto.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            jakarta.persistence.EntityNotFoundException.class,
            faang.school.postservice.exception.EntityNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusNotFound(Exception ex) {
        return ResponseEntity.status(NOT_FOUND).body(getErrorResponse(ex));
    }

    @ExceptionHandler({
            MultipartException.class,
            PostUnverifiedException.class,
            DataValidationException.class,
            JsonSerializationException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusBadRequest(Exception ex) {
        return ResponseEntity.status(BAD_REQUEST).body(getErrorResponse(ex));
    }

    @ExceptionHandler({
            DuplicateEntityException.class,
            PostAlreadyPublishedException.class,
            ConcurrentLikeException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusConflict(Exception ex) {
        return ResponseEntity.status(CONFLICT).body(getErrorResponse(ex));
    }

    @ExceptionHandler({
            HashtagServiceConnectionException.class,
            AsyncPostProcessingException.class
    })
    public ResponseEntity<ErrorResponse> handleExceptionsWithStatusInternalServerError(Exception ex) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(getErrorResponse(ex));
    }

    private ErrorResponse getErrorResponse(Exception ex) {
        log.error("{}", ex.toString());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .build();
    }
}
