package faang.school.postservice.controller.handler;

import faang.school.postservice.dto.error.ErrorResponseDto;
import faang.school.postservice.exception.CommentAlreadyHasPictureException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.PostAlreadyPublishedException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.impl.FileSizeLimitExceededException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(EntityNotFoundException e) {
        log.error("EntityNotFoundException was thrown", e);
        return new ErrorResponseDto(
                HttpStatus.NOT_FOUND.name(),
                "The required object was not found.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            DataValidationException.class,
            FileSizeLimitExceededException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequestExceptions(Exception e) {
        log.error("{}  was thrown", e.getClass().getSimpleName(), e);
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolation(ConstraintViolationException e) {
        log.error("ConstraintViolationException was thrown", e);
        List<String> violations = e.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s", violation.getPropertyPath(), violation.getMessage()))
                .toList();
        return new ErrorResponseDto(
                HttpStatus.BAD_REQUEST.name(),
                "Incorrectly made request.",
                String.join("; ", violations),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(PostAlreadyPublishedException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handlePostAlreadyPublished(PostAlreadyPublishedException e) {
        log.error("PostAlreadyPublishedException was thrown", e);
        return new ErrorResponseDto(
                HttpStatus.CONFLICT.name(),
                "Post was already published.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(CommentAlreadyHasPictureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseDto handleConflictAlreadyBeenDone(Exception e) {
        log.error("{}  was thrown", e.getClass().getSimpleName(), e);
        return new ErrorResponseDto(
                HttpStatus.CONFLICT.name(),
                "This action can only be performed once on an object." +
                        " Undo previous changes to perform this operation again.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ErrorResponseDto handleHttpClientErrorException(HttpClientErrorException e) {
        return new ErrorResponseDto(
                e.getStatusCode().toString(),
                e.getStatusText(),
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDto handleException(Exception e) {
        log.error("Exception was thrown", e);
        return new ErrorResponseDto(
                HttpStatus.INTERNAL_SERVER_ERROR.name(),
                "Something get wrong.",
                e.getMessage(),
                LocalDateTime.now().format(formatter)
        );
    }
}