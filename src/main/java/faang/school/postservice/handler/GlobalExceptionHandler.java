package faang.school.postservice.handler;

import faang.school.postservice.exception.CommentException;
import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.PostException;
import faang.school.postservice.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerUserException(UserException e) {
        log.error("UserException", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(CommentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerCommentException(CommentException e) {
        log.error("CommentException", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(PostException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerPostException(PostException e) {
        log.error("PostException", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerRuntimeException(RuntimeException e) {
        log.error("RuntimeException", e);
        return new ErrorResponse(e.getMessage());
    }
}
