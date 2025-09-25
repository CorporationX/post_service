package faang.school.postservice.exception;

public class UnauthorizedPostAccessException extends PostServiceException {
    public UnauthorizedPostAccessException(Long userId, Long postId) {
        super("User " + userId + " is not authorized to access post " + postId);
    }
}
