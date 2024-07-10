package faang.school.postservice.exception;

public class LockBusyException extends RuntimeException {
    public LockBusyException(String message) {
        super(message);
    }
}
