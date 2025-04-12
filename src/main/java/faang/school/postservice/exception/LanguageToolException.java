package faang.school.postservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class LanguageToolException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public LanguageToolException(String message, HttpStatusCode statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
}
