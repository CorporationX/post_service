package faang.school.postservice.exception;

public class PostNotFoundException extends PostServiceException {
    public PostNotFoundException(Long postId) {
        super("Post not found with ID: " + postId);
    }
}
