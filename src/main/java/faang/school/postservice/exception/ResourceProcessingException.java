package faang.school.postservice.exception;

public class ResourceProcessingException extends RuntimeException {
    public ResourceProcessingException(String message) {
        super(message);
    }
    public ResourceProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
