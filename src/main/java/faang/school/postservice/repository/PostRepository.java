package faang.school.postservice.repository;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.model.Post;
import feign.Param;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.id = :id AND p.deleted = false")
    Optional<Post> findByIdAndNotDeleted(@Param("id") Long id);

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query(nativeQuery = true, value = """
            SELECT * FROM post 
            WHERE hash_tags @> CAST(:hashTag AS jsonb)
            """)
    List<Post> findAllByHashTag(@Param("hashTag") String hashTag);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post
            WHERE hash_tags @> CAST(:hashTag AS jsonb)
            AND published = true
            AND deleted = false
            ORDER BY published_at DESC LIMIT :number
            """)
    List<Post> findTopByHashTagByDate(@Param("hashTag") String hashTag, @Param("number") int number);
}
