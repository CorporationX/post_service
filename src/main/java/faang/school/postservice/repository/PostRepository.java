package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

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

    @Query(nativeQuery = true, value = """
            SELECT c.author_id
            FROM comment c JOIN users u ON c.author_id = u.id
            WHERE c.verified_date IS NOT NULL
                AND c.verified = false
                AND u.banned = false
            GROUP BY c.author_id
            HAVING COUNT(*) > :postCountThreshold
            """)
    List<Long> findAuthorIdsByUnverifiedPostsThreshold(int postCountThreshold);
}
