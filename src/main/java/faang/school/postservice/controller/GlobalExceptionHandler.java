package faang.school.postservice.controller;

import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.ErrorResponseFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e,
                                                               HttpServletRequest req) {
        log.warn("Method argument not valid exception:" + e);
        return ErrorResponseFactory.createNotValid(req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadInput(IllegalArgumentException e, HttpServletRequest req) {
        log.warn("Invalid argument provided occurred:", e);
        return ErrorResponseFactory.create(e, req, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNoSuchElementException(NoSuchElementException e, HttpServletRequest req) {
        log.error("No such element exception occurred:", e);
        return ErrorResponseFactory.create(e, req, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest req) {
        log.error(e.getClass().getSimpleName() + " occurred:", e);
        return ErrorResponseFactory.create(e, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUnexpectedException(Exception e, HttpServletRequest req) {
        log.error(e.getClass().getSimpleName() + " occurred:", e);

        return ErrorResponseFactory.create(e, req, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
