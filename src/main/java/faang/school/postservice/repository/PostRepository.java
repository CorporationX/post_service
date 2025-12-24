package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verified = false")
    Page<Post> findRejected(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.verified IS NULL")
    Page<Post> findUnmoderated(Pageable pageable);

    @Query("""
            SELECT p FROM Post p WHERE p.authorId IN (
                SELECT s.followeeId FROM Subscription s WHERE s.followerId = :followerId
            )
            AND p.published = true 
            AND p.deleted = false
            AND (:lastPublishedAt IS NULL OR p.publishedAt < :lastPublishedAt)
            ORDER BY p.publishedAt DESC
            """)
    List<Post> findFeedPosts(
            @Param("followerId") Long followerId,
            @Param("lastPublishedAt") LocalDateTime lastPublishedAt,
            Pageable pageable
    );

    default Post getByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(
                        () -> new EntityNotFoundException(String.format("Post by id %s not found", id))
                );
    }
}