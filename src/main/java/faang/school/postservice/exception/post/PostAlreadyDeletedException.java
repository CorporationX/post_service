package faang.school.postservice.exception.post;

public class PostAlreadyDeletedException extends RuntimeException {
    public PostAlreadyDeletedException(String message) {
        super(message);
    }
}
