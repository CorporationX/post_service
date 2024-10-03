package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Modifying
    @Transactional
    @Query("UPDATE Post SET content = :content, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    void updateContentByPostId(long id, String content);

    @Modifying
    @Query("UPDATE Post SET deleted = true WHERE id = :id")
    void softDeletePostById(long id);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE author_id = :author_id AND published = false AND deleted = false
            ORDER BY created_at DESC
            """)
    List<Post> findByAuthorIdAndUnpublished(long author_id);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE project_id = :projectId AND published = false AND deleted = false
            ORDER BY created_at DESC
            """)
    List<Post> findByProjectIdAndUnpublished(long projectId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE author_id = :authorId AND published = true AND deleted = false
            ORDER BY published_at DESC
            """)
    List<Post> findByAuthorIdAndPublished(long authorId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE project_id = :projectId AND published = true AND deleted = false
            ORDER BY published_at DESC
            """)
    List<Post> findByProjectIdAndPublished(long projectId);

    @Query(nativeQuery = true, value = """
            SELECT author_id FROM post
            WHERE verified = false AND author_id IS NOT NULL
            GROUP BY author_id
            HAVING COUNT(*) > 5 ;
            """)
    List<Long> findAuthorsWithMoreThanFiveUnverifiedPosts();
}
