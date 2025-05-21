package faang.school.postservice.controller.handler;

import faang.school.postservice.dto.errorresponse.ErrorResponseDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException e) {
        log.error("EntityNotFoundException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            DataValidationException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestExceptions(Exception e) {
        log.error("{} with message {} was thrown", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(PostAlreadyPublishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handlePostAlreadyPublished(PostAlreadyPublishedException e) {
        log.error("PostAlreadyPublishedException with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.CONFLICT.name(),
                "Post was already published.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error("Exception with message {} was thrown", e.getMessage());
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something get wrong.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}