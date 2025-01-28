package faang.school.postservice.exception;

public class KafkaPublishPostException extends RuntimeException {
    public KafkaPublishPostException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
