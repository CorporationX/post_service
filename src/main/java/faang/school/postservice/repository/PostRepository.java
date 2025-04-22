package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id = :postId")
    Optional<Post> findByIdWithLikes(Long postId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query(value = "SELECT * FROM Post p WHERE p.verified = FALSE AND p.verified_date IS NULL ORDER BY p.created_at ASC LIMIT :limit" , nativeQuery = true)
    List<Post> findUnverifiedPosts(@Param("limit") int limit);

    @Query("SELECT p FROM Post p WHERE p.verified = false and p.verifiedDate IS NOT NULL")
    List<Post> findByVerifiedFalse();

    @Modifying
    @Transactional
    @Query(nativeQuery = true,
            value = "DELETE from post_tag WHERE post_tag.post_id = :postId AND post_tag.tag_id in(:tagsId)")
    void deleteTagsFromPost(Long postId, List<Long> tagsId);
}
