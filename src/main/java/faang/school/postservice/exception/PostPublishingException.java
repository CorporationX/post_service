package faang.school.postservice.exception;

public class PostPublishingException extends RuntimeException{
    public PostPublishingException(String message) {
        super(message);
    }

    public PostPublishingException(String message, Throwable cause) {
        super(message, cause);
    }
}
