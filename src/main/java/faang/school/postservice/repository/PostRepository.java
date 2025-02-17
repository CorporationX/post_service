package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
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

    @Query(value = "SELECT p.author_id " +
            "FROM post p WHERE p.verified = false AND p.verified_date IS NOT NULL " +
            "GROUP BY p.author_id " +
            "HAVING COUNT(p.author_id) > :maxPostsToBan",
        nativeQuery = true)
    List<Long> findUserIdsToBanWithUnverifiedPosts(int maxPostsToBan);

    List<Post> findByVerifiedDateIsNull();

    @Query("SELECT p FROM Post p JOIN p.resources r WHERE r.key IN :resourceKeys")
    List<Post> findPostsByResourceKeys(List<String> resourceKeys);
}
