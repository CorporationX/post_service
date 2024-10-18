package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
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

    @Query(value = "SELECT * FROM post WHERE author_id = :authorId ORDER BY published_at DESC LIMIT :limit", nativeQuery = true)
    List<Post> findByAuthorIdLimitAndOrder(long authorId, int limit);

    @Modifying
    @Query("UPDATE Post p SET p.numberViews = p.numberViews + :count WHERE p.id = :postId")
    void updateNumberViews(@Param("postId") Long postId, @Param("count") int count);
}
