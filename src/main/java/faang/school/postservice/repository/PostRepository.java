package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post p
            WHERE p.published = FALSE
            AND p.deleted = FALSE
            AND p.scheduled_at <= now()
            FOR UPDATE SKIP LOCKED
            LIMIT 1000
            """)
    List<Post> findReadyToPublish();

    @Query(nativeQuery = true, value = """
            SELECT * FROM Post p
            WHERE p.published = false AND p.deleted = false AND p.author_id = :authorId
            ORDER BY p.created_at DESC
            """)
    List<Post> findDraftsByAuthorId(Long authorId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM Post p
            WHERE p.published = false AND p.deleted = false AND p.project_id = :projectId
            ORDER BY p.created_at DESC
            """)
    List<Post> findDraftsByProjectId(Long projectId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM Post p
            WHERE p.published = true AND p.deleted = false AND p.author_id = :authorId
            ORDER BY p.published_at DESC
            """)
    List<Post> findPublishedByAuthorId(Long authorId);

    @Query(nativeQuery = true, value = """
            SELECT * FROM Post p
            WHERE p.published = true AND p.deleted = false AND p.project_id = :projectId
            ORDER BY p.published_at DESC
            """)
    List<Post> findPublishedByProjectId(Long projectId);

    @Query(nativeQuery = true , value = """
    SELECT * FROM post
    WHERE published = false AND deleted = false AND corrected_content IS NULL
    FOR UPDATE SKIP LOCKED
    LIMIT :limit
    """)
    List<Post> fetchDraftPostsBatchWithLock(int limit);

    @Query(nativeQuery = true, value = """
    SELECT COUNT(*) FROM post
    WHERE published = false AND deleted = false AND corrected_content IS NULL
    """)
    Long countDraftPosts();

    @Query(nativeQuery = true, value = """
                    SELECT * FROM Post p
                    WHERE p.verified is NULL
                    FOR UPDATE
                    SKIP LOCKED
                    LIMIT :limit
            """)
    List<Post> getNotVerifiedPostsLimitedWithLock(int limit);
    @Query(nativeQuery = true, value = """
    SELECT COUNT(*) FROM post p
    WHERE p.verified is NULL
            """)
    Integer countNotVerifiedPosts();

    @Query(nativeQuery = true, value = """
                    SELECT s.follower_id FROM subscription s
                    WHERE s.followee_id = :authorId
            """)
    List<Long> getAllFollowersOfPostAuthor(Long authorId);
}