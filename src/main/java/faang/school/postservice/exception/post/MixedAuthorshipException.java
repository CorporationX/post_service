package faang.school.postservice.exception.post;

public class MixedAuthorshipException extends RuntimeException {
    private static final String DEFAULT_ERROR_MESSAGE =
            "Both authorId and projectId cannot be filled at the same time. Unclear post authorship";

    public MixedAuthorshipException(String message) {
        super(message);
    }

    public MixedAuthorshipException() {
        super(DEFAULT_ERROR_MESSAGE);
    }
}
