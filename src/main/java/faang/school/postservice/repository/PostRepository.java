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

    void deleteAllByAuthorIdIn(List<Long> authorIds);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    List<Post> findAllByVerified(boolean isVerified);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = false AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findDraftPostsByAuthor(Long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = false AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findDraftPostsByProject(long projectId);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = true AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findPublishedPostsByAuthor(Long authorId);

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.published = true AND p.deleted = false ORDER BY p.createdAt DESC")
    List<Post> findPublishedPostsByProject(long projectId);
}
