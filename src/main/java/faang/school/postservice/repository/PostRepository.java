package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends CrudRepository<Post, Long>, PostRepositoryCustom {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    Optional<Post> findByIdAndDeletedFalse(@NonNull Long postId);

    List<Post> findAllByPublishedFalse();

    default Post findByIdOrThrow(long id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Post no found, id:" + id));
    }

    @Query(value = "SELECT * FROM post WHERE id < :id ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<Post> getPostsAfterIdForFollower(long id, int limit);

    @Query(value = "SELECT * FROM post WHERE id IN :ids", nativeQuery = true)
    List<Post> getByIds(List<Long> ids);
}
