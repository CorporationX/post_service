package faang.school.postservice.rest;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.exception.UnauthorizedException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.util.Utils;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionApiHandler {
    public static final String RUNTIME_ERROR = "Runtime error, see log";
    private final Utils utils;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> detail = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
        String errorMessage = utils.format("Validation failed with {} errors",
                e.getBindingResult().getFieldErrors().size());
        return getErrorResponse("handlerMethodArgumentNotValidException", errorMessage, detail, e);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handlerMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return getErrorResponse("handlerMethodArgumentTypeMismatchException", e);
    }

    @ExceptionHandler(UnrecognizedPropertyException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerUnrecognizedPropertyException(UnrecognizedPropertyException e) {
        return getErrorResponse("handleUnrecognizedPropertyException", e);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationException(ValidationException e) {
        return getErrorResponse("handlerValidationException", e);
    }

    @ExceptionHandler({UserNotFoundException.class, LikeNotFoundException.class, PostNotFoundException.class,
            CommentNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(RuntimeException e) {
        return getErrorResponse("handlerNotFoundException", e);
    }

    @ExceptionHandler(LikeExistsException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public ErrorResponse handlerLikeExistsException(LikeExistsException e) {
        return getErrorResponse("handlerLikeExistsException", e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handlerUnauthorizedException(UnauthorizedException e) {
        return getErrorResponse("handlerUnauthorizedException", e);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handlerRuntimeException(RuntimeException e) {
        return getErrorResponse("handlerRuntimeException", RUNTIME_ERROR, e);
    }

    private ErrorResponse getErrorResponse(String exceptionLabel, Exception e) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    private ErrorResponse getErrorResponse(String exceptionLabel, String errorMessage, Exception e) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponse(errorMessage);
    }

    private ErrorResponse getErrorResponse(
            String exceptionLabel,
            String errorMessage,
            Map<String, String> detail,
            Exception e
    ) {
        log.error("{}: {}", exceptionLabel, e.getMessage(), e);
        return new ErrorResponse(errorMessage, detail);
    }
}
