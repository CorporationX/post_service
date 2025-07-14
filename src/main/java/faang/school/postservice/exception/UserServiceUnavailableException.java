package faang.school.postservice.exception;

public class UserServiceUnavailableException extends DataValidationException {
    public UserServiceUnavailableException(String message) {
        super(message);
    }
}
