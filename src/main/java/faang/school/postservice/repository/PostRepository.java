package faang.school.postservice.repository;

import faang.school.postservice.dto.post.UserPostsDto;
import faang.school.postservice.dto.redis.RedisPostDto;
import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("""
            SELECT new faang.school.postservice.dto.post.UserPostsDto(p.authorId, COUNT(p.id) as count) 
                FROM Post p
                WHERE p.verified = false
                GROUP BY p.authorId 
                HAVING COUNT(p.authorId) > :maxUnverifiedPostsForBan
            """)
    List<UserPostsDto> findUnverifiedPostsCountForUsers(long maxUnverifiedPostsForBan);

    @Query("""
            SELECT new faang.school.postservice.dto.redis.RedisPostDto(
                p.id,
                p.content,
                p.authorId,
                COUNT(l.id),
                COUNT(c.id),
                p.createdAt
            )
            FROM Post p
            LEFT JOIN p.likes l
            LEFT JOIN p.comments c
            WHERE p.authorId = :authorId
            GROUP BY p.id, p.content, p.authorId, p.createdAt
            ORDER BY p.createdAt DESC
            """)
    List<RedisPostDto> findLatestPostsByAuthorId(@Param("authorId")long authorId, Pageable pageable);

    @Query("""
            SELECT new faang.school.postservice.dto.redis.RedisPostDto(
                p.id,
                p.content,
                p.authorId,
                COUNT(l.id),
                COUNT(c.id),
                p.createdAt
            )
            FROM Post p
            LEFT JOIN p.likes l
            LEFT JOIN p.comments c
            WHERE p.id = :postId
            GROUP BY p.id, p.content, p.authorId, p.createdAt
            """)
    Optional<RedisPostDto> findPostForRedisByPostId(@Param("postId")long postId);
}
