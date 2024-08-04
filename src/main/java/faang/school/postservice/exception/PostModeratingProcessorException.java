package faang.school.postservice.exception;

public class PostModeratingProcessorException extends RuntimeException {

    public PostModeratingProcessorException(String message) {
        super(message);
    }

    public PostModeratingProcessorException(String message, Throwable cause) {
        super(message, cause);
    }
}
