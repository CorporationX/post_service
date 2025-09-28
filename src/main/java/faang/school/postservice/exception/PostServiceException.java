package faang.school.postservice.exception;

public abstract class PostServiceException extends RuntimeException {
    public PostServiceException(String message) {
        super(message);
    }
}
