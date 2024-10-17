package faang.school.postservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT p FROM Post p WHERE p.id IN :postIds")
    List<Post> findPostsByIds(List<Long> postIds);

    @Query(nativeQuery = true, value = "SELECT p FROM post p  WHERE p.authorId = :userId AND (:postId IS NULL OR p.id < :postId)" +
            " AND p.published = true AND p.deleted = false ORDER BY p.updated_at DESC LIMIT :countPosts")
    List<Post> findPostIdsByFolloweeId(@Param("followeeId") Long userId, @Param("postId") Long postId, int countPosts);
}
