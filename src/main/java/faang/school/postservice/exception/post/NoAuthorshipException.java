package faang.school.postservice.exception.post;

public class NoAuthorshipException extends RuntimeException {
    private static final String DEFAULT_ERROR_MESSAGE = "Both authorId and projectId cannot be null at the same time.";
    public NoAuthorshipException(String message) {
        super(message);
    }

    public NoAuthorshipException() {
        super(DEFAULT_ERROR_MESSAGE);
    }
}
