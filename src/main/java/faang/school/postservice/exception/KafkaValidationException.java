package faang.school.postservice.exception;

public class KafkaValidationException extends RuntimeException {
    public KafkaValidationException(String message) {
        super(message);
    }
}
