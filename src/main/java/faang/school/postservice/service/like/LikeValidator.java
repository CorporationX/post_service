package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LikeValidator {
  private final static Locale LOCALE = Locale.getDefault();
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final MessageSource messageSource;
  private final UserServiceClient userServiceClient;

  public boolean postLikeValidate(Long postId, Long userId) {
    validateUser(userId);
    Post post = findPostById(postId);
    return post.getLikes().stream().anyMatch(like -> Objects.equals(like.getUserId(), userId));
  }

  public boolean commentLikeValidate(Long commentId, Long userId) {
    validateUser(userId);
    Comment comment = findCommentById(commentId);
    return comment.getLikes().stream().anyMatch(like -> Objects.equals(like.getUserId(), userId));
  }

  private Comment findCommentById(Long commentId) {
    return commentRepository.findById(commentId).orElseThrow(() -> new RuntimeException(
            messageSource.getMessage("exception.comment_is_not_found", new Object[]{commentId}, LOCALE)));
  }

  private Post findPostById(long postId) {
    return postRepository.findById(postId).orElseThrow(() -> new RuntimeException(
            messageSource.getMessage("exception.post_is_not_found", new Object[]{postId}, LOCALE)));
  }

  private void validateUser(long userId) {
    userServiceClient.getUser(userId);
  }
}
