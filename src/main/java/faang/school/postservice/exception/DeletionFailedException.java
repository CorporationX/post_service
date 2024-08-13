package faang.school.postservice.exception;

public class DeletionFailedException extends RuntimeException{
    public DeletionFailedException(String message) {
        super(message);
    }
}
