package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import feign.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= false AND p.deleted = false AND p.authorId = :authorId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByNotPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(long authorId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= false AND p.deleted = false AND p.projectId = :projectId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByNotPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(long projectId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= true AND p.deleted = false AND p.authorId = :authorId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByPublishedAndNotDeletedAndAuthorIdOrderCreatedAtDesc(long authorId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published= true AND p.deleted = false AND p.projectId = :projectId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findByPublishedAndNotDeletedAndProjectIdOrderCreatedAtDesc(long projectId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.published = false")
    List<Post> findByNotPublished();

    @Query("SELECT p FROM Post p " +
            "WHERE p.verified = false")
    List<Post> findByNotVerified();

    @Query(value = "SELECT * FROM post WHERE author_id = :userId ORDER BY created_at DESC",
            nativeQuery = true)
    List<Post> findLatestPostsForUser(@Param("userId") Long userId, Pageable pageable);

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = COALESCE(p.likesCount, 0) + 1 WHERE p.id = :postId")
    void incrementLikesCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.likesCount = p.likesCount - 1 WHERE p.id = :postId")
    void decrementLikesCount(@Param("postId") Long postId);

    @Modifying
    @Query("UPDATE Post p SET p.commentsCount = COALESCE(p.commentsCount, 0) + 1 WHERE p.id = :postId")
    void incrementCommentsCount(@Param("postId") Long postId);

    List<Post> findByAuthorIdIsNotNullAndCreatedAtAfterAndPublishedTrue(LocalDateTime cutoffDate);

    List<Post> findByProjectIdIsNotNullAndCreatedAtAfterAndPublishedTrue(LocalDateTime cutoffDate);
}