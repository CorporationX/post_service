package faang.school.postservice.controller;

import faang.school.postservice.dto.error.ErrorResponseDto;
import faang.school.postservice.exception.DataValidationException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleDataValidationException(DataValidationException e, HttpServletRequest req) {
        log.error("Validation Error", e);
        return new ErrorResponseDto(
                "Validation Error",
                e.getMessage(),
                HttpStatus.BAD_REQUEST.value(),
                req.getRequestURI()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest req) {
        log.error("Requested Entity Not Found", e);
        return new ErrorResponseDto(
                "Requested Entity Not Found",
                e.getMessage(),
                HttpStatus.NOT_FOUND.value(),
                req.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleMethodArgumentNotValidExceptions(MethodArgumentNotValidException e, HttpServletRequest req) {
        log.error("Validation Error", e);
        List<ErrorResponseDto.FieldError> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> new ErrorResponseDto.FieldError(error.getField(), error.getDefaultMessage()))
                .toList();
        return new ErrorResponseDto(
                "Validation Error",
                "Validation failed for one or more fields",
                HttpStatus.BAD_REQUEST.value(),
                req.getRequestURI(),
                errors
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleRuntimeException(RuntimeException e, HttpServletRequest req) {
        log.error("Unexpected Exception", e);
        return new ErrorResponseDto(
                "Unexpected Error Occurred",
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                req.getRequestURI()
        );
    }
}