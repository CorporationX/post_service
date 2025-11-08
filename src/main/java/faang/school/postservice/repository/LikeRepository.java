package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Like;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
    Long countLikeUserForComments(Long userId);

    @Query("""
            SELECT COUNT(l) FROM Like l
            WHERE l.post.id = :postId
            """)
    Long countLikeByPostId(Long postId);

    @Query("""
            SELECT l.post.id, COUNT(l) FROM Like l
            WHERE l.post.id IN :postIds GROUP BY l.post.id
            """)
    List<Object[]> countByPostIds(List<Long> postIds);

    default Map<Long, Long> getLikeCountsByPostIds(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return countByPostIds(postIds).stream()
                .collect(Collectors.toMap(
                        obj -> (Long) obj[0],
                        obj -> (Long) obj[1]
                ));
    }

    @Query("""
            SELECT l.id FROM Like l
            WHERE l.post.id = :postId
            """)
    List<Long> findLikeIdsByPostId(Long postId);

    @Query("""
            SELECT l.post.id, l.id FROM Like l
            "WHERE l.post.id IN :postIds
            """)
    List<Object[]> findLikeIdsByPostIds(List<Long> postIds);

    default Map<Long, List<Long>> getLikeIdsByPostIds(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return findLikeIdsByPostIds(postIds).stream()
                .collect(Collectors.groupingBy(
                        obj -> (Long) obj[0],
                        Collectors.mapping(
                                obj -> (Long) obj[1],
                                Collectors.toList()
                        )
                ));
    }

    @Query("""
            SELECT COUNT(l) FROM Like l
            WHERE l.comment.id = :commentId
            """)
    Long countLikeByComment(Long commentId);


}
