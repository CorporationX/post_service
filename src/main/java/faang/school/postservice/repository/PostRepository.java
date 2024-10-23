package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
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

    @Query(nativeQuery = true, value = "SELECT p.* FROM Post p WHERE p.published = true AND p.id = ?1")
    List<Post> findAllPublishedPostByID(Long postId);

    @Query(nativeQuery = true, value = "SELECT p.updated_at FROM Post p WHERE p.id = :postId")
    Timestamp getUpdatedTime(Long postId);

    @Query(nativeQuery = true, value = "SELECT p.id FROM post p JOIN subscription s ON s.follower_id = p.author_id" +
            " JOIN users u ON s.follower_id = u.id WHERE s.followee_id = :userId AND (:postId IS NULL OR p.id < :postId)" +
            " ORDER BY p.updated_at DESC LIMIT 500")
    List<Long> findPostIdsByFolloweeId(@Param("followeeId") Long userId, @Param("postId") Long postId);
}
