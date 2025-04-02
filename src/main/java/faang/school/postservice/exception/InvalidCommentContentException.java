package faang.school.postservice.exception;

public class InvalidCommentContentException extends IllegalArgumentException {
    public InvalidCommentContentException(String message) {
        super(message);
    }
}
