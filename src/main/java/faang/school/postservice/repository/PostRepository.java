package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT * FROM post p " +
            "WHERE p.author_id IN (:authorIds) " +
            "AND p.deleted = false " +
            "AND (:lastSeenDate IS NULL OR p.published_at < :lastSeenDate)" +
            "ORDER BY p.published_at DESC " +
            "LIMIT :quantity", nativeQuery = true)
    List<Post> findPostsForFeed(List<Long> authorIds, LocalDateTime lastSeenDate, int quantity);

    @Query(value = """
        SELECT * FROM (
            SELECT p.*,
                   ROW_NUMBER() OVER (PARTITION BY p.author_id ORDER BY p.published_at DESC) AS rn
            FROM post p
            WHERE p.author_id IN (:authorIds)
              AND p.deleted = false
        ) sub
        WHERE sub.rn <= :postLimit
        ORDER BY sub.id
        LIMIT :limit OFFSET :offset
        """,
            countQuery = """
        SELECT COUNT(*) FROM (
            SELECT p.id,
                   ROW_NUMBER() OVER (PARTITION BY p.author_id ORDER BY p.published_at DESC) AS rn
            FROM post p
            WHERE p.author_id IN (:authorIds)
              AND p.deleted = false
        ) sub
        WHERE sub.rn <= :postLimit
        """,
            nativeQuery = true)
    Page<Post> findTopPostsByAuthors(List<Long> authorIds, int postLimit, Pageable pageable);

}
