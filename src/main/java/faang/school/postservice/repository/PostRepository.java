package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("""
            SELECT
                p
            FROM
                Post p
            WHERE
                p.published = false
                AND p.deleted = false
                AND p.scheduledAt <= CURRENT_TIMESTAMP
            """)
    List<Post> findReadyToPublish();

    @Query("""
            SELECT
                p.id
            FROM
                Post p
            WHERE
                p.published = true
                AND p.deleted = false
                AND p.authorId = :authorId
                AND p.id < :lastPostId
            ORDER BY
                p.publishedAt DESC
            LIMIT :limit
            """)
    Set<Long> findPostIds(Long authorId, Long lastPostId, int limit);
}
