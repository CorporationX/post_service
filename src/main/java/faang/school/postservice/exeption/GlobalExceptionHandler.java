package faang.school.postservice.exeption;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DataValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(DataValidationException e, HttpServletRequest rq) {
        log.error("[{} {}]", rq.getMethod(), rq.getRequestURL(), e);
        return buildErrorResponse(e, rq, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest rq) {
        log.error("[{} {}]", rq.getMethod(), rq.getRequestURL(), e);
        return e.getBindingResult().getAllErrors().stream()
                .collect(Collectors.toMap(
                        error -> ((FieldError) error).getField(),
                        error -> Objects.requireNonNullElse(error.getDefaultMessage(), "")
                ));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException e, HttpServletRequest rq) {
        log.error("[{} {}]", rq.getMethod(), rq.getRequestURL(), e);
        return buildErrorResponse(e, rq, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleForbiddenException(ForbiddenException e, HttpServletRequest rq) {
        log.error("[{} {}]", rq.getMethod(), rq.getRequestURL(), e);
        return buildErrorResponse(e, rq, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FeignException.class)
    public ErrorResponse handleFeignException(FeignException e, HttpServletRequest rq) {
        log.error("[{} {}]", rq.getMethod(), rq.getRequestURL(), e);
        return buildErrorResponse(e, rq, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleRuntimeException(RuntimeException e, HttpServletRequest rq) {
        log.error("[{} {}]", rq.getMethod(), rq.getRequestURL(), e);
        return buildErrorResponse(e, rq, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ErrorResponse buildErrorResponse(Exception e, HttpServletRequest rq, HttpStatus status) {
        String message = e.getMessage();
        if (e instanceof FeignException) {
            message = cleanFeignMessage(message);
        }
        return new ErrorResponse(
                LocalDateTime.now(),
                rq.getRequestURL().toString(),
                e.getClass().getSimpleName(),
                message,
                status.value()
        );
    }

    private String cleanFeignMessage(String message) {
        if (message == null) return null;

        Matcher pathMatcher = Pattern.compile("\"path\"\\s*:\\s*\"([^\"]+)\"").matcher(message);
        String path = null;
        if (pathMatcher.find()) {
            path = pathMatcher.group(1);
        }

        int bodyStart = message.indexOf("]: [{");
        if (bodyStart != -1) {
            message = message.substring(0, bodyStart + 1);
        }

        StringBuilder result = new StringBuilder(message.trim());
        if (path != null) {
            result.append(", path=").append(path);
        }

        return result.toString()
                .replaceAll("\\s+", " ")
                .trim();
    }

}


