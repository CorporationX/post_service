package faang.school.postservice.exception;

public class PostIdMismatchException extends RuntimeException {
    public PostIdMismatchException(String message) {
        super(message);
    }
}
