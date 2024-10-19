package faang.school.postservice.exceptions;

public class LockException extends RuntimeException {
    public LockException(String message) {
        super(message);
    }
}
