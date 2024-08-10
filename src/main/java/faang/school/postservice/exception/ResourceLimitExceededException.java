package faang.school.postservice.exception;

public class ResourceLimitExceededException extends RuntimeException {
    public ResourceLimitExceededException(String message) {
        super(message);
    }
}
