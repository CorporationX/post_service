package faang.school.postservice.validator.like;

import static faang.school.postservice.exception.ExceptionMessages.DOUBLE_LIKES_FOR_ONE_OBJECT;
import static faang.school.postservice.exception.ExceptionMessages.EXCEPTION_FOR_REPEAT_LIKES;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikesValidation;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CommentLikeValidation extends LikesValidation<Comment, CommentRepository> {

  private static final String COMMENT = "Comment";

  public CommentLikeValidation(LikeRepository likeRepository, CommentRepository repository) {
    super(likeRepository, repository, COMMENT);
  }

  @Override
  public void validateLikes(long id, long userId) {
    checkExistRecord(id);
    checkDoubleLikesForComment(id, userId);
    checkRepeatLikeForComment(id, userId);
  }

  private void checkDoubleLikesForComment(long id, long userId) {
    Function<Like, String> function = like -> String.format(DOUBLE_LIKES_FOR_ONE_OBJECT,
        COMMENT, like.getId(), like.getUserId());
    checkLikes(() -> likeRepository.findByCommentIdAndUserId(id, userId), function);
  }

  private void checkRepeatLikeForComment(long id, long userId) {
    Function<Like, String> function = like -> String.format(EXCEPTION_FOR_REPEAT_LIKES,
        like.getId(), like.getUserId(), COMMENT,
        Optional.ofNullable(like.getPost())
            .map(Post::getId)
            .map(String::valueOf)
            .orElse(ID_NOT_FOUND));

    Supplier<Optional<Like>> likesSupplier = () -> likeRepository.findByCommentIdAndUserId(id, userId)
        .flatMap(like -> Optional.of(like)
            .map(Like::getComment)
            .map(Comment::getPost)
            .map(Post::getLikes)
            .stream()
            .flatMap(Collection::stream)
            .filter(e -> e.getId() == like.getId() && e.getUserId().equals(like.getUserId()))
            .findFirst());
    checkLikes(likesSupplier, function);
  }

}
