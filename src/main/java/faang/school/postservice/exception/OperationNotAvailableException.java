package faang.school.postservice.exception;

public class OperationNotAvailableException extends RuntimeException {
    public OperationNotAvailableException(String message) {
        super(message);
    }
}
