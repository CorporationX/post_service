package faang.school.postservice.exception;

public class ResourceNotOwnedByPostException extends RuntimeException {
    public ResourceNotOwnedByPostException(String message) {
        super(message);
    }
}
