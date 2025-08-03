package faang.school.postservice.repository;

import faang.school.postservice.dto.post.UserPostsDto;
import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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

    @Query("""
            SELECT new faang.school.postservice.dto.post.UserPostsDto(p.authorId, COUNT(p.id) as count) 
                FROM Post p
                WHERE p.verified = false
                GROUP BY p.authorId 
                HAVING COUNT(p.authorId) > :maxUnverifiedPostsForBan
            """)
    List<UserPostsDto> findUnverifiedPostsCountForUsers(long maxUnverifiedPostsForBan);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = true ORDER BY p.publishedAt DESC")
    List<Post> findTopByAuthorIdAndPublishedTrueOrderByPublishedAtDesc(
            @Param("authorId") Long authorId,
            Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.authorId = :authorId AND p.published = true AND p.publishedAt < :publishedAt ORDER BY p.publishedAt DESC")
    List<Post> findTopByAuthorIdAndPublishedTrueAndPublishedAtBeforeOrderByPublishedAtDesc(
            @Param("authorId") Long authorId,
            @Param("publishedAt") LocalDateTime publishedAt,
            Pageable pageable);
}
