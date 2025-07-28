package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query(
        "SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP"
    )
    List<Post> findReadyToPublish();

    default List<Post> findDraftsByUserId(Long userId) {
        return findAll(
                PostSpecifications.byAuthorId(userId)
                        .and(PostSpecifications.drafts()),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    default List<Post> findPublishedByUserId(Long userId) {
        return findAll(
                PostSpecifications.byAuthorId(userId)
                        .and(PostSpecifications.published()),
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );
    }

    default List<Post> findDraftsByProjectId(Long projectId) {
        return findAll(
                PostSpecifications.byProjectId(projectId)
                        .and(PostSpecifications.drafts()),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }

    default List<Post> findPublishedByProjectId(Long projectId) {
        return findAll(
                PostSpecifications.byProjectId(projectId)
                        .and(PostSpecifications.published()),
                Sort.by(Sort.Direction.DESC, "publishedAt")
        );
    }



}
