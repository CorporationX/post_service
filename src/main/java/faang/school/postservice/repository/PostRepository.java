package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    List<Post> findAllByIdInOrderByCreatedAtDesc(List<Long> ids);

    List<Post> findByPublishedFalseAndAuthorIdAndDeletedFalseOrderByCreatedAtDesc(long authorId);

    List<Post> findByPublishedFalseAndProjectIdAndDeletedFalseOrderByCreatedAtDesc(long projectId);

    List<Post> findByPublishedTrueAndAuthorIdAndDeletedFalseOrderByPublishedAtDesc(long authorId);

    List<Post> findByPublishedTrueAndProjectIdAndDeletedFalseOrderByPublishedAtDesc(long projectId);

    @Query(value = "SELECT DISTINCT author_id FROM post WHERE author_id IS NOT NULL", nativeQuery = true)
    List<Long> findAllDistinctAuthorIds();

    @Query(value = """

            SELECT * FROM post 
        WHERE author_id IN :authorIds 
        AND published = true 
        AND deleted = false 
        AND published_at IS NOT NULL
        ORDER BY published_at DESC
        LIMIT :limit
        """, nativeQuery = true)
    List<Post> findLatestPublishedByAuthorIds(
        @Param("authorIds") List<Long> authorIds,
        @Param("limit") int limit
    );
}