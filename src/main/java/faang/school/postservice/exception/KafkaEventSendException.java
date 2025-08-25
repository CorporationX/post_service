package faang.school.postservice.exception;

public class KafkaEventSendException extends RuntimeException {
    public KafkaEventSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
