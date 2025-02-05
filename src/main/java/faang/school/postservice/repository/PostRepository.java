package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("""
    SELECT p FROM Post p
    WHERE p.authorId = :authorId AND p.published = false AND p.deleted = false
    ORDER BY p.createdAt DESC
    """)
    List<Post> findAllDraftsByAuthorId(long authorId);

    @Query("""
    SELECT p FROM Post p
    WHERE p.projectId = :projectId AND p.published = false AND p.deleted = false
    ORDER BY p.createdAt DESC
    """)
    List<Post> findAllDraftsByProjectId(long projectId);

    @Query("""
    SELECT p FROM Post p
    WHERE p.authorId = :authorId AND p.published = true AND p.deleted = false
    ORDER BY p.publishedAt DESC
    """)
    List<Post> findAllPublishedByAuthorId(long authorId);

    @Query("""
    SELECT p FROM Post p
    WHERE p.projectId = :projectId AND p.published = true AND p.deleted = false
    ORDER BY p.publishedAt DESC
    """)
    List<Post> findAllPublishedByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    Page<Post> findReadyToPublish(Pageable pageable);
}
