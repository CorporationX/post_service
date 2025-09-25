package faang.school.postservice.exception;

public class PostAlreadyDeletedException extends PostServiceException {
    public PostAlreadyDeletedException(Long postId) {
        super("Post with ID " + postId + " is already deleted");
    }
}
