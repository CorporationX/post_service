package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
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

    @Query("""
            SELECT p.authorId
            FROM Post p
            WHERE p.verifiedStatus = 'REJECTED'
            GROUP BY p.authorId
            HAVING COUNT(p) >= :minRejectedPosts
            """)
    List<Long> findAuthorIdsWithMinRejectedPosts(@Param("minRejectedPosts") int minRejectedPosts);

    List<Post> findAllByIdIn(List<Long> ids);

    @Query("""
        SELECT p FROM Post p
        WHERE p.authorId IN :authorIds
        ORDER BY p.publishedAt DESC
    """)
    List<Post> findPostsByAuthorIds(@Param("authorIds") Collection<Long> authorIds);

    long findViewsCountById(long postId);

    List<Post> findPostsByIdIn(List<Long> postIds);
}
