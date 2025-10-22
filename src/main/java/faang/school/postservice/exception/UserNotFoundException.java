package faang.school.postservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(long userId) {
        super("User " + userId + " not found");
    }
}