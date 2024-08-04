package faang.school.postservice.exception;

public class ModerationSchedulerException extends RuntimeException {

    public ModerationSchedulerException(String message) {
        super(message);
    }

    public ModerationSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
