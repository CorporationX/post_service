package faang.school.postservice.exception;

public class InvalidPostDataException extends RuntimeException {
    public InvalidPostDataException(String message) {
        super(message);
    }

    public InvalidPostDataException(String message, Throwable cause) {
        super(message, cause);
    }
}