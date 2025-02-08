package faang.school.postservice.repository;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query("SELECT p FROM Post p WHERE p.published = false")
    List<Post> findByPublishedFalse();

    @Query(value = "SELECT s.follower_id " +
                   "FROM post p " +
                   "JOIN users u ON p.author_id = u.id " +
                   "JOIN subscription s ON s.followee_id = u.id " +
                   "WHERE p.author_id = :authorId", nativeQuery = true)
    List<Long> findFollowersByAuthorId(Long authorId);

    @Query(value = """
            SELECT *
            FROM post
            WHERE published = true AND deleted = false AND author_id IN (:authors)
            ORDER BY id DESC
            LIMIT :postCount
            """,
            nativeQuery = true
    )
    List<Post> findByAuthorsId(@Param("authors") List<Long> authors, @Param("postCount") int postCount);

    default Post getPostById(Long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Post with id " + id + " not found"));
    }

    List<Post> findByVerifiedIsNull();

}
