package faang.school.postservice.exception;

public class OutboxPublishException extends RuntimeException {

    public OutboxPublishException(String message) {
        super(message);
    }

    public OutboxPublishException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutboxPublishException(Throwable cause) {
        super(cause);
    }
}
