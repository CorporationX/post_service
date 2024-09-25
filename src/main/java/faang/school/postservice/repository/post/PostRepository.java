package faang.school.postservice.repository.post;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthorId(long authorId);

    @Query("SELECT p FROM Post p " +
            "WHERE p.updatedAt < (SELECT p2.updatedAt FROM Post p2 WHERE p2.id = :postId) " +
            "AND p.authorId IN :authorIds " +
            "ORDER BY p.updatedAt DESC")
    List<Post> findPreviousFeedPostsByAuthorIdAndPostId(Long postId, List<Long> authorIds, Pageable pageable);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate = null AND p.deleted = false")
    List<Post> findNotVerified();

    @Modifying
    @Query("UPDATE Post p SET p.published = :published, p.publishedAt = :publishedTime WHERE p.id = :postId")
    int updatePublishedStatus(@Param("postId") Long postId, @Param("publishedTime") LocalDateTime publishedTime, @Param("published") Boolean published);
}
