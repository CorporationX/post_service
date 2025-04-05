package faang.school.postservice.handler;

import faang.school.postservice.dto.ErrorDto;
import faang.school.postservice.exception.ApiError;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.exception.DataFetchException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.UploadFileException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataFetchException.class)
    public ResponseEntity<String> handleDataFetchException(DataFetchException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(e.getMessage());
    }
    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDataValidationException(DataValidationException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto("Data validation exception", ex.getMessage());
    }

    @ExceptionHandler(UploadFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleFileIsEmptyException(UploadFileException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto("Upload file exception", ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto("Upload file size is bigger than allowed", ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleEntityNotFoundException(EntityNotFoundException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto("Entity not found exception", ex.getMessage());
    }

    @ExceptionHandler(CommentValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleCommentValidationException(CommentValidationException ex) {
        log.error("Comment validation failure", ex);
        return new ErrorDto("Comment validation failure", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {

        List<ApiError.FieldErrorDetail> validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ApiError.FieldErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.toList());

        return new ApiError("Validation failed", validationErrors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConstraintViolationException(ConstraintViolationException ex) {

        log.warn("Constraint violation exception: {}", ex.getMessage(), ex);

        List<ApiError.FieldErrorDetail> errors = ex.getConstraintViolations().stream()
                .map(violation -> {
                    log.warn("Constraint violation: {} - {}", violation.getPropertyPath(), violation.getMessage());
                    return new ApiError.FieldErrorDetail(
                            violation.getPropertyPath().toString(),
                            violation.getMessage()
                    );
                })
                .collect(Collectors.toList());

        return new ApiError("Constraint violation", errors);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleThrowable(Throwable t) {
        log.error(t.getMessage(), t);
        return new ErrorDto("Interaction failure", t.getMessage());
    }
}
