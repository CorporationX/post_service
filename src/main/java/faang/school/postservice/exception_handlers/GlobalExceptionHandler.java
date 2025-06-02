package faang.school.postservice.exception_handlers;

import faang.school.postservice.exception.AuthorNotFoundException;
import faang.school.postservice.exception.DataAccessException;
import faang.school.postservice.exception.UsersGettingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccessExceptions(MethodArgumentNotValidException ex) {
        var message = "Data access error: %s".formatted(ex.getMessage());
        log.error(message, ex);

        return ResponseEntity.internalServerError().body(message);
    }

    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<Object> handleAuthorNotFoundExceptions(AuthorNotFoundException ex) {
        log.error(ex.getMessage(), ex);

        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UsersGettingException.class)
    public ResponseEntity<Object> handleUsersGettingExceptions(UsersGettingException ex) {
        log.error(ex.getMessage(), ex);

        return ResponseEntity.internalServerError().body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleExceptions(MethodArgumentNotValidException ex) {
        var message = "Unknown server error: %s".formatted(ex.getMessage());
        log.error(message, ex);

        return ResponseEntity.internalServerError().body(message);
    }
}
