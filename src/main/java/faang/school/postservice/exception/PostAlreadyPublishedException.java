package faang.school.postservice.exception;

public class PostAlreadyPublishedException extends RuntimeException {
    public PostAlreadyPublishedException(String message) {
        super(message);
    }
}