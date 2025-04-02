package faang.school.postservice.exception;

public class NotAuthorException extends IllegalArgumentException {
    public NotAuthorException(String message) {
        super(message);
    }
}
