package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import feign.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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

    @Query("SELECT p FROM Post p WHERE p.verified IS NULL OR p.verifiedDate < p.updatedAt")
    List<Post> findAllUnverifiedPosts();

    @Query("SELECT p.authorId FROM Post p WHERE p.verified = false GROUP BY p.authorId HAVING COUNT(p.authorId) > 5")
    List<Long> findAuthorsToBan();

    @Modifying
    @Transactional
    @Query(value = "UPDATE post SET verified = :verified, verified_Date = :verifiedDate WHERE id = :id", nativeQuery = true)
    void setVerifiedAndVerifiedDate(@Param("id") long id, @Param("verified") boolean verified, @Param("verifiedDate") LocalDateTime verifiedDate);
}
