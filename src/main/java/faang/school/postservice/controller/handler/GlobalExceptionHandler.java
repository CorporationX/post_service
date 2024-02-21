package faang.school.postservice.controller.handler;

import faang.school.postservice.exception.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(AlreadyDeletedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyDeletedException(AlreadyDeletedException e) {
        log.error("Already deleted exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler(AlreadyPostedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyPostedException(AlreadyPostedException e) {
        log.error("Already posted exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler(EmptyContentInPostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmptyContentInPostException(EmptyContentInPostException e) {
        log.error("Empty content in post exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e) {
        log.error("Entity not found exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler(NoPublishedPostException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNoPublishedPostException(NoPublishedPostException e) {
        log.error("Post isn't published exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED, LocalDateTime.now());
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUserNotFoundException(UserNotFoundException e) {
        log.error("User not found exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(SamePostAuthorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleSamePostAuthorException(SamePostAuthorException e) {
        log.error("Same author of the post exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(UpdatePostException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleUpdatePostException(UpdatePostException e) {
        log.error("Update exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e){
        log.error("Data validation exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST, LocalDateTime.now());
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFoundException(DataNotFoundException e){
        log.error("Data not found exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND, LocalDateTime.now());
    }

    @ExceptionHandler(SameTimeActionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleSameTimeActionException(SameTimeActionException e){
        log.error("Same time action exception", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.CONFLICT, LocalDateTime.now());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleException(Exception e) {
        log.error("EXCEPTION", e);
        return new ErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
    }
}
