package faang.school.postservice.exception;

import faang.school.postservice.dto.error.ErrorDto;
import faang.school.postservice.exception.post.ImmutablePostDataException;
import faang.school.postservice.exception.post.PostAlreadyDeletedException;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.post.PostWOAuthorException;
import faang.school.postservice.exception.post.PostWithTwoAuthorsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.UUID;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception) {

        ErrorDto errorDto = new ErrorDto();
        errorDto.setId(UUID.randomUUID());
        errorDto.setMessage(exception.getMessage());

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRunTimeException(
            RuntimeException exception) {
        ErrorDto errorDto = new ErrorDto();
        errorDto.setId(UUID.randomUUID());
        errorDto.setMessage(exception.getMessage());

        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PostAlreadyPublishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlePostAlreadyPublishedException(PostAlreadyPublishedException e) {
        log.error("The post has already been published", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PostWOAuthorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostWOAuthorException(PostWOAuthorException e) {
        log.error("The post without author.", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("The entity not found.", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ImmutablePostDataException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleImmutablePostDataException(ImmutablePostDataException e) {
        log.error("Attempt to modify immutable data.", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PostWithTwoAuthorsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostWithTwoAuthorsException(PostWithTwoAuthorsException e) {
        log.error("The post has two authors.", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PostAlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostAlreadyDeletedException(PostAlreadyDeletedException e) {
        log.error("The post is already deleted.", e);
        return new ErrorResponse(e.getMessage());
    }
}
