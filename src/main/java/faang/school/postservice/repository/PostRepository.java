package faang.school.postservice.repository;

import faang.school.postservice.model.entity.Post;
import feign.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.deleted = false")
    Optional<Post> findByIdAndNotDeleted(@Param("id") Long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.published = false" +
            " AND p.deleted = false" +
            " AND p.scheduledAt < CURRENT_TIMESTAMP" +
            " AND p.spellCheckCompleted = false")
    List<Post> findReadyForSpellCheck();

    @Query(value = "SELECT p FROM post p JOIN p.hashtags h WHERE h.id = :hashtagId", nativeQuery = true)
    List<Post> findByHashtagId(Long hashtagId);

    Page<Post> findByHashtagsContent(String content, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM post_album WHERE album_id = :albumId AND post_id = :postId")
    boolean existsInAlbum(long albumId, long postId);

    List<Post> findAllByVerifiedDateIsNull();

    @Query("SELECT p.authorId " +
            "FROM Post p " +
            "WHERE p.verified = false AND p.deleted = false " +
            "AND p.authorId BETWEEN :minAuthorId AND :maxAuthorId " +
            "GROUP BY p.authorId " +
            "HAVING COUNT(p) > 5")
    List<Long> findAuthorsWithMoreThanFiveUnverifiedPostsInRange(Long minAuthorId, Long maxAuthorId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId AND p.publishedAt BETWEEN :startDate AND :endDate")
    List<Post> getUserPublishedPostsByDateRange(@Param("authorId") Long authorId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);

    @Query(nativeQuery = true, value = "SELECT p.* FROM post p WHERE p.author_id IN (:authorIds) order by published_at desc offset :offset limit :limit")
    List<Post> findAllByAuthorIdIn(List<Long> authorIds, int offset, int limit);
}
