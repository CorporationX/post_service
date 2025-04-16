package faang.school.postservice.exception;

public class KafkaMessageSerializationException extends RuntimeException {

    public KafkaMessageSerializationException(ExceptionMessage message, Throwable cause) {
        super(message.formatMessage(), cause);
    }
}
