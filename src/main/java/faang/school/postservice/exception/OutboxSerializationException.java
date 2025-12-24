package faang.school.postservice.exception;

public class OutboxSerializationException extends RuntimeException {
    public OutboxSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
