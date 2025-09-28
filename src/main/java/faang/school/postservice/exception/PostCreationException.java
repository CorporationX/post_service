package faang.school.postservice.exception;

public class PostCreationException extends PostServiceException {
    public PostCreationException(Long authorId) {
        super("Failed to create post for author with ID: " + authorId);
    }
}
