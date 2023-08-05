package faang.school.postservice.exception;

public class AlreadyDeletedException extends RuntimeException {

    public AlreadyDeletedException(String message) {
        super(message);
    }
}
