package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    @Query("""
                    SELECT p
                    FROM Post p
                    WHERE p.authorId = :authorId
                      AND p.deleted = false
                      AND p.published = :published
                    ORDER BY CASE
                                 WHEN p.published = true THEN p.publishedAt
                                 ELSE p.createdAt
                                 END desc
            """)
    List<Post> findByAuthorId(long authorId, boolean published);

    @Query("""
                SELECT p
                    FROM Post p
                    WHERE p.projectId = :projectId
                      AND p.deleted = false
                      AND p.published = :published
                    ORDER BY CASE
                                 WHEN p.published = true THEN p.publishedAt
                                 ELSE p.createdAt
                                 END desc
            """)
    List<Post> findByProjectId(long projectId, boolean published);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= " +
            "CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query(nativeQuery = true, value = """
            UPDATE Post SET published = true, published_at = CURRENT_TIMESTAMP
            where deleted = false and scheduled_at <= CURRENT_TIMESTAMP
                        """)
    @Modifying
    int publishingPostsOnSchedule();
}
