package faang.school.postservice.validator;

import static faang.school.postservice.exception.ExceptionMessages.RECORD_NOT_EXIST;

import faang.school.postservice.exception.like.LikeValidationException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
public abstract class LikesValidation<T, D extends CrudRepository<T, Long>> {

  public static final String ID_NOT_FOUND = "id not found";

  protected final LikeRepository likeRepository;
  private final D repository;
  private String exceptionMessagePrefix;

  protected LikesValidation(LikeRepository likeRepository, D repository,
      String exceptionMessagePrefix) {
    this.likeRepository = likeRepository;
    this.repository = repository;
    this.exceptionMessagePrefix = exceptionMessagePrefix;
  }

  public abstract void validateLikes(long id, long userId);

  @Transactional(readOnly = true)
  public void checkExistRecord(long id) {
    if (repository.findById(id).isEmpty()) {
      failedValidateMessage(String.format(RECORD_NOT_EXIST, exceptionMessagePrefix, id));
    }
  }

  @Transactional(readOnly = true)
  protected void checkLikes(Supplier<Optional<Like>> likeSupplier,
      Function<Like, String> likeFunction) {
    likeSupplier.get()
        .ifPresent(like -> {
          failedValidateMessage(likeFunction.apply(like));
        });
  }

  private void failedValidateMessage(String message) {
    log.warn(message);
    throw new LikeValidationException(message);
  }

}
