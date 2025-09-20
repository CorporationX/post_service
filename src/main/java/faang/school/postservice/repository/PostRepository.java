package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long>, PostRepositoryCustom {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    Optional<Post> findByIdAndDeletedFalse(@NonNull Long postId);

    List<Post> findAllByPublishedFalse();

    @Query(value = """
    (
      SELECT s.follower_id
      FROM subscription s
      WHERE (:authorId IS NOT NULL AND s.followee_id = :authorId)
    )
    UNION
    (
      SELECT ps.follower_id
      FROM project_subscription ps
      WHERE (:projectId IS NOT NULL AND ps.project_id = :projectId)
    )
    """, nativeQuery = true)
    List<Long> findAllFollowers(@Param("authorId") Long authorId,
                                @Param("projectId") Long projectId);
}
