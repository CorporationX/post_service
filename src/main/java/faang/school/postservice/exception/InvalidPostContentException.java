package faang.school.postservice.exception;

public class InvalidPostContentException extends PostServiceException {
    public InvalidPostContentException(String reason) {
        super("Invalid post content: " + reason);
    }
}
