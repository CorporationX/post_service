package faang.school.postservice.exception;

public class UserNotFoundException extends DataValidationException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
