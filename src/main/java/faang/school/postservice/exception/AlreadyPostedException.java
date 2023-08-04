package faang.school.postservice.exception;

public class AlreadyPostedException extends RuntimeException {

    public AlreadyPostedException(String message) {
        super(message);
    }
}
