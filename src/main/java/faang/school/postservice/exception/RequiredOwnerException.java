package faang.school.postservice.exception;

public class RequiredOwnerException extends RuntimeException {
    public RequiredOwnerException(String message) {
        super(message);
    }
}
