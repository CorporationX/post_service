package faang.school.postservice.exception;

public class NotFoundElementException extends RuntimeException {

    public NotFoundElementException(String message) {
        super(message);
    }

    public NotFoundElementException(String message, Throwable cause) {
        super(message, cause);
    }
}
