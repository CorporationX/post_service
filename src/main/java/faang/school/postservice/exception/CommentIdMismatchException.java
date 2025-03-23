package faang.school.postservice.exception;

public class CommentIdMismatchException extends RuntimeException {
    public CommentIdMismatchException(String message) {
        super(message);
    }
}
