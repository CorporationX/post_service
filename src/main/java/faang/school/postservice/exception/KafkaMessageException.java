package faang.school.postservice.exception;

public class KafkaMessageException extends RuntimeException {

    public KafkaMessageException(String message) {
        super(message);
    }
}
