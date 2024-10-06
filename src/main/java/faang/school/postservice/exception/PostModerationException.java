package faang.school.postservice.exception;

public class PostModerationException extends RuntimeException {
    public PostModerationException(String message) {
        super(message);
    }

    public PostModerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
