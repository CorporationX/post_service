package faang.school.postservice.handler;

import faang.school.postservice.exception.ExternalServiceException;
import faang.school.postservice.exception.PostValidationException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGenericException(Exception e) {
        log.error("An error occurred: ", e);
        return new ErrorResponse("An unexpected error occurred", e.getMessage());
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDataAccessException(DataAccessException e) {
        log.error("Database error occurred: ", e);
        return new ErrorResponse("Database error", "An error occurred while accessing the database.");
    }

    @ExceptionHandler(PostValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostValidationException(PostValidationException e) {
        log.error("Post validation failure", e);
        return new ErrorResponse("Post validation failure", e.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity Not Found", e);
        return new ErrorResponse("Entity Not Found", e.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleExternalServiceException(ExternalServiceException e) {
        log.error("Interaction failure", e);
        return new ErrorResponse("Interaction failure", e.getMessage());
    }
}