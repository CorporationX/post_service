package faang.school.postservice.repository;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP""")
    List<Post> findReadyToPublish();

    default Post getByIdOrThrow(long postId) {
        return findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post %d not found", postId)));
    }

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.published = true AND p.deleted = false AND p.authorId = :authorId""")
    List<Post> findPostToPublishedByAuthorId(Long authorId);

    @Query(value = """
            SELECT p FROM Post p
            WHERE p.published = false AND p.deleted = false AND p.authorId = :authorId""")
    List<Post> findPostToDraftByAuthorId(Long authorId);

    default Post findPostWithLikesOrThrow(long postId) {
        return findPostWithLikes(postId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post %d not found", postId)));
    }

    @Query("""
            SELECT p FROM Post p 
            LEFT JOIN FETCH p.likes 
            WHERE p.id = :postId
            """)
    Optional<Post> findPostWithLikes(Long postId);

    @Query("""
        SELECT p FROM Post p
        LEFT JOIN Like l ON p.id = l.postId
        WHERE p.id IN :postIds
        """)
    List<Post> getPostsWithLikes(List<Long> postIds, Pageable pageable);
}
