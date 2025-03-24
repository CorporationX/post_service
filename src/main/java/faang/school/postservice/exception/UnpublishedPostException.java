package faang.school.postservice.exception;

public class UnpublishedPostException extends RuntimeException {
    public UnpublishedPostException(String message) {
        super(message);
    }
}
