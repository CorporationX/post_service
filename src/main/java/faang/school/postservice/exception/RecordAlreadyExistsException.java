package faang.school.postservice.exception;

public class RecordAlreadyExistsException extends IllegalStateException {
    public RecordAlreadyExistsException(String message) {
        super(message);
    }
}
