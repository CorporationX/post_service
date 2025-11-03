package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends CrudRepository<Like, Long> {
    @Transactional
    void deleteByPostIdAndUserId(long postId, long userId);

    @Transactional
    void deleteByCommentIdAndUserId(long commentId, long userId);

    default Like findByPostIdAndUserIdOrThrow(long postId, long userId) {
        return findByPostIdAndUserId(postId, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Like not found for post %d and user %d ", postId, userId))
        );
    }

    default Like findByCommentIdAndUserIdOrThrow(long commentId, long userId) {
        return findByCommentIdAndUserId(commentId, userId).orElseThrow(
                () -> new EntityNotFoundException(
                        String.format("Like not found for post %d and user %d ", commentId, userId))
        );
    }

    Optional<Like> findByPostIdAndUserId(long postId, long userId);

    Optional<Like> findByCommentIdAndUserId(long commentId, long userId);

    List<Like> findAllByPostId(long postId);

    List<Like> findAllByCommentId(long commentId);

    List<Like> findAllByUserId(long userId);

    @Query("""
            SELECT l FROM Like l
            WHERE l.userId = :userId AND
            l.post IS NOT NULL
            """)
    List<Like> findByUserIdAndPostIsNotNull(Long userId);

    @Query("""
            SELECT l FROM Like l
            WHERE l.userId = :userId AND
            l.comment IS NOT NULL
            """)
    List<Like> findByUserIdAndCommentIsNotNull(Long userId);

    @Query("""
            SELECT COUNT(l) FROM Like l
            WHERE l.userId = :userId AND
            l.post.id IS NOT NULL
            """)
    Integer countLikeUserForPosts(Long userId);

    @Query("""
            SELECT COUNT(l) FROM Like l
            WHERE l.userId = :userId AND
            l.comment.id IS NOT NULL
            """)
    Integer countLikeUserForComments(Long userId);

    @Query("""
            SELECT COUNT(l) FROM Like l
            WHERE l.post.id = :postId
            """)
    Integer countLikeByPost(Long postId);

    @Query("""
            SELECT COUNT(l) FROM Like l
            WHERE l.comment.id = :commentId
            """)
    Integer countLikeByComment(Long commentId);


}
