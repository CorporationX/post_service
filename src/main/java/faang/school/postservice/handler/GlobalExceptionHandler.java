package faang.school.postservice.handler;

import faang.school.postservice.dto.ErrorDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.post.ImmutablePostDataException;
import faang.school.postservice.exception.post.PostAlreadyDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import faang.school.postservice.exception.post.PostWithoutAuthorException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("Method argument not valid exception", e);
        List<String> errors = new ArrayList<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(fieldName + " - " + errorMessage);
        });
        return new ErrorDto(errors.toString());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleConstraintViolationException(ConstraintViolationException e) {
        log.error("Constraint violation exception", e);
        List<String> errors = new ArrayList<>();
        e.getConstraintViolations()
                .forEach(error -> errors.add(error.getPropertyPath().toString() + " - " + error.getMessage()));
        return new ErrorDto(errors.toString());
    }

    @ExceptionHandler(PostAlreadyPublishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handlePostAlreadyPublishedException(PostAlreadyPublishedException e) {
        log.error("Post already published exception", e);
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(PostWithoutAuthorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handlePostWithoutAuthorException(PostWithoutAuthorException e) {
        log.error("Post without author exception", e);
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found exception", e);
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(ImmutablePostDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleImmutablePostDataException(ImmutablePostDataException e) {
        log.error("Immutable post data exception", e);
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(PostWithTwoAuthorsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handlePostWithTwoAuthorsException(PostWithTwoAuthorsException e) {
        log.error("Post with two authors exception", e);
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(PostAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handlePostAlreadyDeletedException(PostAlreadyDeletedException e) {
        log.error("Post already deleted exception", e);
        return new ErrorDto(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ErrorDto handleRuntimeException(RuntimeException e) {
        log.error("Runtime exception", e);
        return new ErrorDto(e.getMessage());
    }
}
