package faang.school.postservice.controller;

import faang.school.postservice.exception.comment.CommentExceptionHandler;
import faang.school.postservice.exception.post.PostAlreadyPublished;
import faang.school.postservice.exception.post.PostDeletedException;
import faang.school.postservice.exception.post.UnexistentPostException;
import faang.school.postservice.exception.post.UnexistentPostPublisher;
import faang.school.postservice.exception.resource.UnexistentResourceException;
import faang.school.postservice.exception.validation.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class PostServiceExceptionHandler {

    @ExceptionHandler({DataValidationException.class, UnexistentPostPublisher.class, CommentExceptionHandler.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseException handleDataValidationException(DataValidationException e) {
        log.info(e.getMessage(), e);
        return buildErrorResponseFromException(
                HttpStatus.BAD_REQUEST, e
        );
    }

    @ExceptionHandler({UnexistentPostException.class, UnexistentResourceException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseException handleUnexistentPostException(Exception e) {
        log.error(e.getMessage(), e);
        return buildErrorResponseFromException(
                HttpStatus.NOT_FOUND, e
        );
    }

    @ExceptionHandler({PostAlreadyPublished.class, PostDeletedException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseException handleConflictExceptions(Exception e) {
        log.error(e.getMessage(), e);
        return buildErrorResponseFromException(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseException handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        Map<String, String> invalidFields = e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        objectError -> Objects.requireNonNullElse(
                                objectError.getDefaultMessage(),
                                "invalid")
                        )
                );

        return buildMapErrorResponse(
                HttpStatus.BAD_REQUEST,
                "validation failed",
                e,
                "invalid fields",
                invalidFields
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseException handleException(Exception e) {
        log.error(e.getMessage(), e);
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An internal error occurred while processing your request."
        );

        addProperties(detail);

        return new ErrorResponseException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                detail,
                e
        );
    }

    private ErrorResponseException buildErrorResponseFromException(HttpStatus status, Throwable cause) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                status,
                cause.getMessage()
        );

        addProperties(detail);

        return new ErrorResponseException(
                status,
                detail,
                cause
        );
    }

    private void addProperties(ProblemDetail detail){
        detail.setType(URI.create("error"));
        detail.setProperty("date", LocalDateTime.now());
    }

    private ErrorResponseException buildMapErrorResponse(
            HttpStatus status,
            String errorDescription,
            Throwable cause,
            String mapName,
            Map<String, String> map) {

        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
                status,
                errorDescription != null ? errorDescription : cause.getMessage()
        );

        addProperties(detail);

        detail.setProperty(mapName, map);

        return new ErrorResponseException(
                status,
                detail,
                cause
        );
    }
}
