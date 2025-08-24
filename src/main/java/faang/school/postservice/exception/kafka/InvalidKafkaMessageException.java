package faang.school.postservice.exception.kafka;

public class InvalidKafkaMessageException extends RuntimeException {
    public InvalidKafkaMessageException(String msg, Throwable ex) {
        super(msg, ex);
    }
}
