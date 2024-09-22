package faang.school.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.id IN :postIds")
    List<Post> findPostsByIds(List<Long> postIds);

    @Query(value = "SELECT * FROM Post p " +
            "WHERE p.published = true " +
            "AND p.deleted = false " +
            "AND p.author_id in :authorIds " +
            "ORDER BY p.published_at DESC " +
            "OFFSET :startPostId ROWS " +
            "LIMIT :batchSize ", nativeQuery = true)
    List<Post> findPostsByAuthorIds(List<Long> authorIds, long startPostId, long batchSize);
}
