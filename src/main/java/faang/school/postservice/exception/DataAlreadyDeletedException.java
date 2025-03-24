package faang.school.postservice.exception;

public class DataAlreadyDeletedException extends RuntimeException {
    public DataAlreadyDeletedException(String message) {
        super(message);
    }
}
