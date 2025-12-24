package faang.school.postservice.exception;

public class EventPublishingException extends RuntimeException {
    public EventPublishingException(String message) {
        super(message);
    }
}
