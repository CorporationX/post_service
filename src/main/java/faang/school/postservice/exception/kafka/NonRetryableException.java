package faang.school.postservice.exception.kafka;

public class NonRetryableException extends RuntimeException {
    public NonRetryableException(String message) {
        super(message);
    }
}
