package faang.school.postservice.exception;

public class ValidationServiceExceptions extends RuntimeException {

    public ValidationServiceExceptions(String message) {
        super(message);
    }

    public ValidationServiceExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
