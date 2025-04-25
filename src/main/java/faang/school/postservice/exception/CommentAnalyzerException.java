package faang.school.postservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class CommentAnalyzerException extends RuntimeException {
    private final HttpStatusCode statusCode;

    public CommentAnalyzerException(String message, HttpStatusCode statusCode) {
        super(message + " (HTTP " + statusCode + ")");
        this.statusCode = statusCode;
    }
}
