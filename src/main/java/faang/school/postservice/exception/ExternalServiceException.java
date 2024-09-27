package faang.school.postservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ExternalServiceException extends RuntimeException {
    private final HttpStatus status;
    public ExternalServiceException(HttpStatus status, String format, Object... args) {
        super(String.format(format, args));
        this.status = status;
    }
}
