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
    List<Post> findAllByVerifiedAtIsNull();

    @Query(nativeQuery = true, value = """
            SELECT p.* FROM Post p
            JOIN post_hashtag ph ON p.id = ph.post_id
            JOIN hashtags h ON ph.hashtag_id = h.id
            WHERE h.hashtag = :hashtag AND p.published = TRUE AND p.deleted = FALSE
            ORDER BY p.published_at DESC
            """)
    List<Post> findByHashtagOrderByDate(String hashtag);


    @Query(nativeQuery = true, value = """
            SELECT p.*
            FROM Post p
            JOIN post_hashtag ph ON p.id = ph.post_id
            JOIN likes l ON p.id = l.post_id
            JOIN comment c ON p.id = c.post_id
            JOIN hashtags h ON ph.hashtag_id = h.id
            WHERE h.hashtag = :hashtag AND p.published = TRUE AND p.deleted = FALSE
            GROUP BY p.id, p.content, p.author_id, p.project_id, p.published, p.published_at, p.scheduled_at, p.deleted, p.created_at, p.updated_at
            ORDER BY COUNT(l.id) + COUNT(c.id) DESC
            """)
    List<Post> findByHashtagOrderByPopularity(String hashtag);

    List<Post> findAllByVerifiedFalseAndVerifiedAtIsNotNull();

    List<Post> findAllByPublishedFalseAndDeletedFalse();
}