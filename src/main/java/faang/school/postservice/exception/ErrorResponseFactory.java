package faang.school.postservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Optional;

public class ErrorResponseFactory {
    private ErrorResponseFactory() {
    }

    public static ErrorResponse create(Exception e, HttpServletRequest req, HttpStatus status) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(safeUrl(req))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(safeMessage(e, status))
                .build();
    }

    public static ErrorResponse createNotValid(HttpServletRequest req, HttpStatus status) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .url(safeUrl(req))
                .status(status.value())
                .error(status.getReasonPhrase())
                .message("Validation failed")
                .build();
    }

    private static String safeMessage(Throwable e, HttpStatus status) {
        if (e.getClass() == NullPointerException.class) {
            return e.getMessage();
        }

        if (status.is5xxServerError()) {
            return "Internal server error";
        }

        return Optional.ofNullable(e.getMessage())
                .filter(s -> !s.isBlank())
                .orElse("Unknown error");
    }

    private static String safeUrl(HttpServletRequest req) {
        return Optional.ofNullable(req)
                .map(r -> {
                    String uri = r.getRequestURI();
                    String queryString = r.getQueryString();
                    return queryString == null ? uri : uri + "?" + queryString;
                })
                .orElse("N/A");
    }
}
