package faang.school.postservice.exception;

import faang.school.postservice.dto.error.ErrorResponse;
import faang.school.postservice.exceptions.AsyncPostProcessingException;
import faang.school.postservice.exceptions.PostAlreadyPublishedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            jakarta.persistence.EntityNotFoundException.class,
            faang.school.postservice.exception.EntityNotFoundException.class,
    })
    @ResponseStatus(NOT_FOUND)
    public ErrorResponse handleExceptionsWithStatusNotFound(Exception ex) {
        return getErrorResponse(ex, NOT_FOUND);
    }

    @ExceptionHandler({
            MultipartException.class,
            ConcurrentLikeException.class,
            DataValidationException.class,
            DuplicateEntityException.class,
            PostUnverifiedException.class,
            AsyncPostProcessingException.class,
            PostAlreadyPublishedException.class
    })
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse handleExceptionsWithStatusBadRequest(Exception ex) {
        return getErrorResponse(ex, BAD_REQUEST);
    }

    private ErrorResponse getErrorResponse(Exception ex, HttpStatus status) {
        log.error("{}", ex.toString());
        return ErrorResponse.builder()
                .message(ex.getMessage())
                .statusCode(status.value())
                .statusName(status.name())
                .build();
    }
}
