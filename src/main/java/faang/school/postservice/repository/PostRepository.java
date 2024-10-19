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

    @Query(nativeQuery = true, value = """
            SELECT * FROM post p
            WHERE p.author_id IN (:authorIds)
            AND p.id < :lastPostId
            ORDER BY p.id DESC
            LIMIT :postsCount""")
    List<Post> findByAuthorsBeforeId(List<Long> authorIds, Long lastPostId, int postsCount);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post p
            WHERE p.author_id IN (:authorIds)
            ORDER BY p.id DESC
            LIMIT :postsCount""")
    List<Post> findByAuthors(List<Long> authorIds, int postsCount);

    @Query(nativeQuery = true, value = """
            UPDATE post
            SET views = views + :incValue
            WHERE id = :id
            RETURNING views
            """)
    long incrementAndGetViewsById(long id, int incValue);

    @Query(nativeQuery = true, value = """
            SELECT p.id FROM post p
            WHERE p.author_id IN (
                SELECT s.followee_id FROM subscription s
                WHERE s.follower_id = :followerId
            )
            ORDER BY p.id DESC
            LIMIT :batchSize
            """)
    List<Long> findPostIdsByFollowerId(Long followerId, int batchSize);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.id IN (:ids)")
    List<Post> findAllByIdsWithLikes(Iterable<Long> ids);
}
