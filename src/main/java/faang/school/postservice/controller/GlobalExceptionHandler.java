package faang.school.postservice.controller;

import faang.school.postservice.exceptions.DataNotFoundException;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.exceptions.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e){
        log.error("Data validation exception", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDataNotFoundException(DataNotFoundException e){
        log.error("Data not found exception", e);
        return new ErrorResponse(e.getMessage());
    }
}
