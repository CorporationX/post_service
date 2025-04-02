package faang.school.postservice.exception;

public class NullEntityException extends IllegalArgumentException {
    public NullEntityException(String message) {
        super(message);
    }
}
