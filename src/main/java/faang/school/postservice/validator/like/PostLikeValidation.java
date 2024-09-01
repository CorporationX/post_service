package faang.school.postservice.validator.like;

import static faang.school.postservice.exception.ExceptionMessages.DOUBLE_LIKES_FOR_ONE_OBJECT;
import static faang.school.postservice.exception.ExceptionMessages.EXCEPTION_FOR_REPEAT_LIKES;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.LikesValidation;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostLikeValidation extends LikesValidation<Post, PostRepository> {

  private static final String POST = "Post";

  public PostLikeValidation(LikeRepository likeRepository, PostRepository repository) {
    super(likeRepository, repository, POST);
  }

  @Override
  public void validateLikes(long id, long userId) {
    checkExistRecord(id);
    checkDoubleLikesForPost(id, userId);
    checkRepeatLikeForPost(id, userId);
  }

  private void checkDoubleLikesForPost(long id, long userId) {
    Function<Like, String> function = like -> String.format(DOUBLE_LIKES_FOR_ONE_OBJECT,
        POST, like.getId(), like.getUserId());
    checkLikes(() -> likeRepository.findByPostIdAndUserId(id, userId), function);
  }

  private void checkRepeatLikeForPost(long id, long userId) {
    Function<Like, String> function = like -> String.format(EXCEPTION_FOR_REPEAT_LIKES,
        like.getId(), like.getUserId(), POST,
        Optional.ofNullable(like.getComment())
            .map(Comment::getId)
            .map(String::valueOf)
            .orElse(ID_NOT_FOUND));

    Supplier<Optional<Like>> likesSupplier = () -> likeRepository.findByPostIdAndUserId(id, userId)
        .flatMap(like -> Optional.of(like)
            .map(Like::getPost)
            .map(Post::getComments)
            .stream()
            .flatMap(Collection::stream)
            .map(Comment::getLikes)
            .flatMap(Collection::stream)
            .filter(e -> e.getId() == like.getId() && e.getUserId().equals(like.getUserId()))
            .findFirst()
        );
    checkLikes(likesSupplier, function);
  }

}
