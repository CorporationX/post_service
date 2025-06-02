package faang.school.postservice.exception;

public class LikeException extends RuntimeException {
    public LikeException(String message) {
        super(message);
    }

    public LikeException(String message, Throwable cause) {
        super(message, cause);
    }
}
