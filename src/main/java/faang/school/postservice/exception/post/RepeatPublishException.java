package faang.school.postservice.exception.post;

public class RepeatPublishException extends RuntimeException {
    public RepeatPublishException(String message) {
        super(message);
    }
}
