package faang.school.postservice.exception.post;

public class UserBanException extends RuntimeException {
    public UserBanException(String message, Throwable cause) {
        super(message, cause);
    }
}