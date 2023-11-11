package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = true AND p.deleted = false")
    List<Post> findAllAuthorPublished(long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = true AND p.deleted = false")
    List<Post> findAllProjectPublished(long projectId);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = false AND p.deleted = false")
    List<Post> findAllUsersDrafts(long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = false AND p.deleted = false")
    List<Post> findAllProjectDrafts(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    List<Post> findByVerifiedIsFalse();

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= :time")
    List<Post> findAllPostsByTimeAndStatus(@Param("time") LocalDateTime time);
}
