package faang.school.postservice.exception.post;

public class PostAlreadyPublishedException extends RuntimeException {
    public PostAlreadyPublishedException(String message) {
        super(message);
    }
}
