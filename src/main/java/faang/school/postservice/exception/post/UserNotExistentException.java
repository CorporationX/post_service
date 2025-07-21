package faang.school.postservice.exception.post;

public class UserNotExistentException extends RuntimeException {
    public UserNotExistentException(String message) {
        super(message);
    }
}
