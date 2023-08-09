package faang.school.postservice.exception;

public class SamePostAuthorException extends RuntimeException {

    public SamePostAuthorException(String message) {
        super(message);
    }
}
