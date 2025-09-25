package faang.school.postservice.exception;

public class PostAlreadyPublishedException extends PostServiceException {
  public PostAlreadyPublishedException(Long postId) {
    super("Post " + postId + " is already published");
  }
}
