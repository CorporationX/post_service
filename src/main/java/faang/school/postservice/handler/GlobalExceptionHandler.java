package faang.school.postservice.handler;

import faang.school.postservice.dto.ErrorDto;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleDataValidationException(DataValidationException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorDto("Data validation exception", ex.getMessage());
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

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleThrowable(Throwable t) {
        log.error(t.getMessage(), t);
        return new ErrorDto("Interaction failure", t.getMessage());
    }
}
