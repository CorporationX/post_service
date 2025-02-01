package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate = NULL OR p.updatedAt >= :lastDayDate")
    Optional<List<Post>> findNotCheckedToVerificationPosts(@Param("lastDayDate") LocalDateTime lastDayDate);

    @Query("SELECT p FROM Post p WHERE p.verified = FALSE")
    Optional<List<Post>> findNotVerifiedPots();

    @Query(value = """
            SELECT p.id FROM Post p
            WHERE p.authorId IN(:follweeIds)
            ORDER BY p.publishedAt desc LIMIT :limit
            """)
    Optional<Set<Long>> findLastFolloweePostIds(@Param("follweeIds") List<Long> follweeIds, @Param("limit") int limit);

    @Query("SELECT p FROM Post p WHERE p.id IN (:postIds)")
    Optional<List<Post>> findAllByIds(@Param("postIds") Set<Long> postIds);
}
