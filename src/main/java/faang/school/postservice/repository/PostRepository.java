package faang.school.postservice.repository;

import faang.school.postservice.model.post.CachePost;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import faang.school.postservice.model.post.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByAuthorId(long authorId, PageRequest pageRequest);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.id IN :postIds")
    List<Post> findPostsByIds(List<Long> postIds);

    @Query("SELECT p FROM Post p WHERE p.authorId IN :authorsIds order by p.createdAt DESC")
    List<Post> findPostsByAuthorIds(@Param("authorsIds") List<Long> authorIds, Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id = :postId")
    Optional<Post> findByIdWithLikes(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.comments WHERE p.id = :postId")
    Optional<Post> findByIdWithComments(@Param("postId") Long postId);
}
