package faang.school.postservice.repository;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.deleted = :deleted AND p.published = :published")
    Page<Post> findByAuthorIdAndDeletedStatusAndPublished(@Param("authorId") long authorId,
            @Param("deleted") boolean deleted,
            @Param("published") boolean published,
            @Param("pageable") Pageable pageable
            );

    @Query("SELECT p FROM Post p WHERE p.projectId = :projectId AND p.deleted = :deleted AND p.published = :published")
    Page<Post> findByProjectIdAndDeletedStatusAndPublished(@Param("projectId") long projectId,
            @Param("deleted") boolean deleted,
            @Param("published") boolean published,
            @Param("pageable") Pageable pageable
            );

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("""
       SELECT DISTINCT p.authorId
       FROM Post p
       WHERE p.verified = false
       AND (
           SELECT COUNT(p2) 
           FROM Post p2 
           WHERE p2.authorId = p.authorId AND p2.verified = false
       ) > :threshold
       """)
    List<Long> findUsersToBanForPosts(@Param("threshold") int threshold);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId")
    Page<Comment> findAllCommentByPostId(@Param("postId") Long postId, Pageable pageable);
}
