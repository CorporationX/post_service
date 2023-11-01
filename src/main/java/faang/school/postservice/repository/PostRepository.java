package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(@Param("projectId") long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(@Param("authorId") long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate = null OR p.updatedAt >= p.verifiedDate")
    List<Post> findNotVerified();

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.corrected = false")
    List<Post> findNotPublished();

    @Query("SELECT p FROM Post p WHERE p.authorId IN (:followeesIds) AND p.published = true AND p.deleted = false ORDER BY p.publishedAt DESC LIMIT :limit")
    List<Post> findSortedPostsByAuthorIdsAndLimit(@Param("followeesIds") List<Long> followeesIds, @Param("limit") long limit);

    @Query("SELECT p FROM Post p WHERE p.authorId IN (:followeesIds) AND p.id NOT IN (:postIds) AND p.published = true AND p.deleted = false ORDER BY p.publishedAt DESC LIMIT :limit")
    List<Post> findSortedPostsByAuthorIdsNotInPostIdsLimit(@Param("followeesIds") List<Long> followeesIds, @Param("postIds") List<Long> postIds, @Param("limit") long limit);

    @Query("SELECT p FROM Post p WHERE p.authorId IN (:followees) AND p.deleted = false AND p.publishedAt < :lastPostDate ORDER BY p.publishedAt DESC LIMIT :limit")
    List<Post> findSortedPostsFromPostDateAndAuthorsLimit(@Param("followees") List<Long> followees, @Param("lastPostDate") LocalDateTime lastPostDate, @Param("limit") int limit);

    @Query("SELECT p FROM Post p WHERE p.id = :postId AND p.published = true AND p.deleted = false")
    Optional<Post> findPublishedAndNotDeletedBy(long postId);

    @Query("UPDATE Post SET views = views + 1 WHERE id = :postId")
    @Modifying
    void incrementPostViewByPostId(@Param("postId") long postId);

    @Query("UPDATE Post SET views = views + 1 WHERE authorId = :authorId")
    @Modifying
    void incrementPostViewByAuthorId(@Param("authorId") long authorId);
}