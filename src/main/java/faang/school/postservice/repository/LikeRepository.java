package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Transactional
    void deleteByCommentIdAndUserId(long commentId, long userId);

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    List<Like> findByPostId(Long postId);

    List<Like> findByCommentId(Long commentId);

    @Query(nativeQuery = true, value = """
            SELECT post_id FROM likes
            WHERE likes.user_id = :userId
            """)
    List<Long> findPostIdsByUserId(@Param("userId") Long userId);

    boolean existsByPostIdAndUserId(@NotNull Long postId, @NotNull Long userId);

    boolean existsByCommentIdAndUserId(@NotNull Long commentId, @NotNull Long userId);

    default Like findByIdOrThrow(Long likeId) {
        return findById(likeId).orElseThrow(() -> new EntityNotFoundException(
                String.format("Лайк с id = %d не найден", likeId)
                )
        );
    }
}
