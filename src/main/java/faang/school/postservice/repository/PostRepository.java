package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import feign.Param;
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

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedAt IS NULL")
    List<Post> findByVerifiedAtIsNull();

    @Query(value = """
    SELECT * FROM post
    WHERE author_id IN (:authorIds)
    ORDER BY created_at DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<Post> findLatestPostsByAuthors(
            @Param("authorIds") List<Long> authorIds,
            @Param("limit") int limit
    );
}
