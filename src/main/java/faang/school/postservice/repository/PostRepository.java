package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND " +
            "p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query(nativeQuery = true,  value = """
            SELECT post.* FROM post
            JOIN subscription ON subscription.followee_id = post.author_id
            WHERE subscription.follower_id = :userId AND post.id < :postId
            ORDER BY post.created_at DESC
            LIMIT :limit
            """)
    List<Post> findPostByFollowerId(Long userId, Long postId, Long limit);

    @Query(nativeQuery = true, value = """
            select s.follower_id from subscription s
            where followee_id = :followeeId
            """)
    List<Long> findAllIdFollowerFollowee(Long followeeId);
}
