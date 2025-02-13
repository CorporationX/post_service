package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.authorId = " +
            ":authorId AND p.scheduledAt <= CURRENT_TIMESTAMP ORDER BY p.createdAt DESC")
    List<Post> findReadyToPublishByAuthor(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.projectId = " +
            ":projectId AND p.scheduledAt <= CURRENT_TIMESTAMP ORDER BY p.createdAt DESC")
    List<Post> findReadyToPublishByProject(long projectId);

    @Query("SELECT p FROM Post p WHERE p.published = true AND p.deleted = false AND p.authorId = " +
            ":authorId ORDER BY p.publishedAt DESC")
    List<Post> findPublishedByAuthor(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = true AND p.deleted = false AND p.projectId = " +
            ":projectId ORDER BY p.publishedAt DESC")
    List<Post> findPublishedByProject(long projectId);

    @Query("SELECT p FROM Post p JOIN p.hashtags h WHERE p.published = true AND p.deleted = false " +
            "AND h.tag = :tag ORDER BY p.publishedAt DESC")
    List<Post> findByHashtags(String tag);

    @Query("SELECT p.authorId, COUNT(p) FROM Post p WHERE p.verified = false and p.authorId IS NOT NULL GROUP BY p.authorId")
    List<Object[]> findUnverifiedPostsGroupedByAuthor();

    List<Post> findAllByVerifiedDateIsNull();

//    Optional<Post> findById(long id);
}
