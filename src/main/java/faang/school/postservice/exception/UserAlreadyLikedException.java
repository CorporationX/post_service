package faang.school.postservice.exception;

public class UserAlreadyLikedException extends RuntimeException {
    public UserAlreadyLikedException(String message) {
        super(message);
    }
}
