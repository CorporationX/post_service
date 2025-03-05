package faang.school.postservice.exception;

public class FeedAccessDeniedException extends RuntimeException {
    public FeedAccessDeniedException(String message) {
        super(message);
    }
}