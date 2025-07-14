package faang.school.postservice.repository.post;

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

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Post findByPostId(long postId);

    @Query(nativeQuery = true, value = """
    SELECT p.* FROM Post p
    WHERE p.author_id IN (:authorIds)
    AND p.id < :lastPostId
    Order BY p.id DESC
    LIMIT :limit
    """)
    List<Post> findByAuthorIds(List<Long> authorIds, Long lastPostId, int limit);

    @Query(nativeQuery = true, value = """
            SELECT p.* FROM Post p WHERE p.created_at >= NOW() - (INTERVAL '1 day' * :days)""")
    Page<Post> findRecentPosts(Pageable pageable, @Param("days") int days);
}
