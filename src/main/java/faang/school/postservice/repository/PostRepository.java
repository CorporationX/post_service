package faang.school.postservice.repository;

import faang.school.postservice.dto.post.PostFilterDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
                WHERE p.projectId = :projectId
            """)
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
                WHERE p.authorId = :authorId
            """)
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("""
            SELECT p FROM Post p
            WHERE p.published = false
                AND p.deleted = false
                AND p.scheduledAt <= CURRENT_TIMESTAMP
            """
    )
    List<Post> findReadyToPublish();

    default Post findPostOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Пост с id " + id + " не найден"));
    }

    default Page<Post> findByFilter(PostFilterDto filter, Pageable pageable) {
        Specification<Post> spec = PostSpecificationBuilder.buildSpecification(filter);
        return findAll(spec, pageable);
    }
}
