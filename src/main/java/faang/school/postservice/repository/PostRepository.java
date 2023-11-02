package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(@Param("projectId") long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(@Param("authorId") long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate = null OR p.updatedAt >= p.verifiedDate")
    List<Post> findNotVerified();

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.corrected = false")
    List<Post> findNotPublished();

    default List<Post> getPostsByFollowees(List<Long> followees, int postQuantity, EntityManager entityManager) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p WHERE p.authorId IN :followees AND p.deleted = false ORDER BY p.publishedAt DESC", Post.class);
        query.setParameter("followees", followees);
        query.setMaxResults(postQuantity);

        return query.getResultList();
    }

    default List<Post> getNextPostsByFollowees(List<Long> followees, int postQuantity,
                                           EntityManager entityManager, LocalDateTime previousPostDate) {
        TypedQuery<Post> query = entityManager.createQuery(
                "SELECT p FROM Post p WHERE p.authorId IN (:followees) AND p.deleted = false " +
                        "AND p.publishedAt < :previousPostDate ORDER BY p.publishedAt DESC", Post.class);
        query.setParameter("followees", followees);
        query.setParameter("previousPostDate", previousPostDate);
        query.setMaxResults(postQuantity);

        return query.getResultList();
    }
}
