package faang.school.postservice.exception;

public class KafkaPostPublisherException extends RuntimeException {
    public KafkaPostPublisherException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
