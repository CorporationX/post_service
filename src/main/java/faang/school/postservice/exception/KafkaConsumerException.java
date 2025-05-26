package faang.school.postservice.exception;

public class KafkaConsumerException extends RuntimeException {
    public KafkaConsumerException(String message) {
        super(message);
    }
}
