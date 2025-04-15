package faang.school.postservice.exceptions;

public class AsyncPostProcessingException extends RuntimeException {

    public AsyncPostProcessingException(String message) {
        super(message);
    }

    public AsyncPostProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
