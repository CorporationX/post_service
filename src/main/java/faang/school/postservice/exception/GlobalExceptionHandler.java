package faang.school.postservice.exception;

import faang.school.postservice.dto.response.ConstraintErrorResponse;
import faang.school.postservice.dto.response.ErrorResponse;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.exception.validation.Violation;
import feign.FeignException;
import faang.school.postservice.dto.response.ValidationErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PostAlreadyPublishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExceptionWithBadRequest(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler({EntityNotFoundException.class, FeignException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleExceptionWithNotFound(RuntimeException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ConstraintErrorResponse handleConstraintValidationException(ConstraintViolationException ex) {
        final List<Violation> violations = ex.getConstraintViolations().stream()
                .map(violation -> new Violation(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .toList();
        log.error(ex.getMessage(), ex);
        return new ConstraintErrorResponse(violations);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ConstraintErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        final List<Violation> violations = new java.util.ArrayList<>(ex.getBindingResult().getFieldErrors().stream()
                .map(violation -> new Violation(
                        violation.getField(),
                        violation.getDefaultMessage()
                ))
                .toList());
        violations.addAll(ex.getBindingResult().getGlobalErrors().stream()
                .map(violation -> new Violation(
                        "",
                        violation.getDefaultMessage()
                ))
                .toList());
        log.error(ex.getMessage(), ex);
        return new ConstraintErrorResponse(violations);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error(ex.getMessage(), ex);
        return new ErrorResponse(ex.getMessage());
    }
@ExceptionHandler(Throwable.class)
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public ErrorResponse handleOtherExceptions(Throwable ex) {
    log.error(ex.getMessage(), ex);
    return new ErrorResponse(ex.getMessage());
}
}
