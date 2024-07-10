package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate IS NULL OR p.verifyStatus = 'UNCHECKED'")
    List<Post> findNotVerifiedPosts();

    @Query("SELECT p.id FROM Post p WHERE p.authorId = :authorId ORDER BY p.id DESC")
    List<Long> findPostIdsByAuthorIdOrderByIdDesc(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = true AND p.deleted = false AND p.authorId IN :authors AND p.id >= :postId ORDER BY p.id DESC LIMIT :countPosts")
    List<Post> findByAuthorsAndLimitAndStartFromPostId(List<Long> authors, int countPosts, long postId);

    @Query("SELECT p.id FROM Post p WHERE p.published = true AND p.deleted = false")
    List<Long> findAllIds();
}
