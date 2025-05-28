package faang.school.postservice.repository.post;

import faang.school.postservice.model.post.Post;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    @Query(nativeQuery = true, value = """
            SELECT p
            FROM post p
            WHERE p.published = false
            AND p.deleted = false
            AND (p.user_id = :user_id OR p.project_id = :project_id)
            ORDER BY created_at DESC
            """)
    List<Post> findAllDraftPostsByUserOrProject(@Param("user_id") Long userId, @Param("project_id") Long projectId);

    @Query(nativeQuery = true, value = """
            SELECT p
            FROM post p
            WHERE p.published = true
            AND p.deleted = false
            AND (p.user_id = :user_id OR p.project_id = :project_id)
            ORDER BY published_at DESC
            """)
    List<Post> findAllPublishedPostsByUserOrProject(@Param("user_id") Long userId, @Param("project_id") Long projectId);

    List<Post> findByProjectId(long projectId);

    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.FETCH)
    @Query(nativeQuery = true, value = """
            SELECT p
            FROM Post p
            WHERE p.project_id = :project_id
            """)
    List<Post> findByProjectIdWithLikes(@Param("project_id") long projectId);

    @EntityGraph(attributePaths = "likes", type = EntityGraph.EntityGraphType.FETCH)
    @Query(nativeQuery = true, value = """
            SELECT p
            FROM Post p
            WHERE p.author_id = :author_id
            """)
    List<Post> findByAuthorIdWithLikes(@Param("author_id") long authorId);

    @Query("""
            SELECT p
            FROM Post p
            WHERE p.published = false
            AND p.deleted = false
            AND p.scheduledAt <= CURRENT_TIMESTAMP
            """)
    List<Post> findReadyToPublish();

}
