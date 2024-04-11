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

    @Query("SELECT p FROM Post p WHERE p.verifiedDate IS NULL")
    List<Post> findAllByVerifiedDateIsNull();

    @Query(value = "WITH ranked_posts AS (" +
            "  SELECT *, ROW_NUMBER() OVER(PARTITION BY author_id ORDER BY published_at DESC) as rn" +
            "  FROM post" +
            "  WHERE author_id IN :authorIds" +
            ")" +
            "SELECT * FROM ranked_posts WHERE rn <= :limit",
            nativeQuery = true)
    List<Post> findLastPostsByAuthorIds(List<Long> authorIds, int limit);
}
