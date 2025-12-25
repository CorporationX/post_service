package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    List<Post> findAllByPublishedFalseAndDeletedFalse();

    @Query(value = """
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
            WHERE p.projectId = :projectId
            """)
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP""")
    List<Post> findReadyToPublish();

    default Post getByIdOrThrow(long postId) {
        return findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post %d not found", postId)));
    }

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.published = true AND p.deleted = false AND p.authorId = :authorId""")
    List<Post> findPostToPublishedByAuthorId(Long authorId);

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.published = false AND p.deleted = false AND p.authorId = :authorId""")
    List<Post> findPostToDraftByAuthorId(Long authorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p FROM Post p 
            WHERE p.published = false AND p.deleted = false
            """)
    List<Post> findUnpublished();

    default Post findPostWithLikesAndCommentOrThrow(long postId) {
        return findPostWithLikesAndComment(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post %d not found", postId)));
    }

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
            LEFT JOIN FETCH p.comments
            WHERE p.id = :postId
            """)
    Optional<Post> findPostWithLikesAndComment(Long postId);

    default Long findAuthorIdByIdOrThrow(Long postId) {
        return findAuthorIdById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post %d not found", postId)));
    }

    @Query("""
            SELECT p.authorId
            FROM Post p 
            WHERE p.id = :postId""")
    Optional<Long> findAuthorIdById(Long postId);

    @Query("""
            SELECT p.id FROM Post p 
            WHERE p.published = true 
            AND p.deleted = false 
            AND (:lastPostId IS NULL OR p.id < :lastPostId)
            ORDER BY p.publishedAt DESC 
            LIMIT :limit
            """)
    List<Long> findPublishedPostsAfterCursor(@Param("lastPostId") Long lastPostId, @Param("limit") int limit);
}
