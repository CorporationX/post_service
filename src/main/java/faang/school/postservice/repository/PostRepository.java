package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id = :id")
    Optional<Post> findByIdWithLikes(long id);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId AND p.published = true "
            + "AND p.deleted = false ORDER BY p.publishedAt DESC")
    List<Post> findByProjectIdWithLikesOrderByPublishDateDesc(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId AND p.published = true "
            + "AND p.deleted = false ORDER BY p.publishedAt DESC")
    List<Post> findByAuthorIdWithLikesOrderByPublishDateDesc(long authorId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId AND p.published = false "
            + "AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findDraftsByProjectIdWithLikesOrderByCreationDateDesc(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId AND p.published = false "
            + "AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findDraftsByAuthorIdWithLikesOrderByCreationDateDesc(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();
}
