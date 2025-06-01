package faang.school.postservice.exception.post;

public class PostAlreadyPublishedException extends RuntimeException {
    public PostAlreadyPublishedException(long postId) {
        super(String.format("Post with id %d already published", postId));
    }
}
