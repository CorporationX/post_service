package faang.school.postservice.exception.event;

public class EventPublishingException extends RuntimeException {

  public EventPublishingException(String message, Throwable cause) {
    super(message, cause);
  }

}
