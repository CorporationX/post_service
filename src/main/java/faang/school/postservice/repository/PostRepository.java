package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("""
            SELECT p FROM Post p 
            WHERE p.published = false 
                AND p.deleted = false 
                AND p.scheduledAt <= CURRENT_TIMESTAMP
            """)
    List<Post> findReadyToPublish();

    @Query(nativeQuery = true, value = """
            WITH author_post AS (
                SELECT * FROM post
                WHERE author_id IN (:authorIds)
                AND published = 'true'
                ORDER BY published_at DESC
            )
            SELECT * FROM author_post
            WHERE :lastPostId IS NULL
                OR published_at < (
                    SELECT published_at FROM author_post
                    WHERE id = :lastPostId
                )
            LIMIT :batch
            """)
    List<Post> getPublishedRangeByAuthorsDesc(List<Long> authorIds, Long lastPostId, int batch);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            ORDER BY published_at DESC
            LIMIT :batch
            """)
    List<Post> findAnyRecentBatch(int batch);
}
