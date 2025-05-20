package faang.school.postservice.exception;

public class LikeExistsException extends RuntimeException {
    public LikeExistsException(String message) {
        super(message);
    }
}
