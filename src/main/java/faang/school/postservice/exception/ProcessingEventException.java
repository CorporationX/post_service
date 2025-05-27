package faang.school.postservice.exception;

public class ProcessingEventException extends RuntimeException {
    public ProcessingEventException(String message) {
        super(message);
    }
    public ProcessingEventException(String message, Throwable cause) {
        super(message, cause);
    }
}
