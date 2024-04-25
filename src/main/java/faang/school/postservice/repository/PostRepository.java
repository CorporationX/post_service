package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByAuthorId(long authorId);

    List<Post> findByProjectId(long projectId);

    @Query("SELECT p.authorId, COUNT(p) FROM Post p WHERE p.verified = false " +
            "AND p.authorId IS NOT NULL GROUP BY p.authorId HAVING COUNT(p) > :count")
    List<Long> findAuthorIdsByNotVerifiedPosts(int count);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.projectId = :projectId")
    List<Post> findByProjectIdWithLikes(long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    default List<Post> getNextPostsByAuthorIds(EntityManager entityManager,
                                               List<Long> authorIds, LocalDateTime previousPostDate, int postQuantity) {
        TypedQuery<Post> query;
        if (previousPostDate == null) {
            query = entityManager.createQuery(
                    """
                            SELECT p FROM Post p
                            WHERE p.authorId IN (:authorIds)
                            ORDER BY p.publishedAt ASC
                            """
                    , Post.class);
        } else {
            query = entityManager.createQuery(
                    """
                            SELECT p FROM Post p
                            WHERE p.authorId IN (:authorIds) AND p.publishedAt < :previousPostDate
                            ORDER BY p.publishedAt ASC
                            """
                    , Post.class);
            query.setParameter("previousPostDate", previousPostDate);
        }

        query.setParameter("authorIds", authorIds);
        query.setMaxResults(postQuantity);
        return query.getResultList();
    }

    default List<Post> getNextPostsByAuthorId(EntityManager entityManager,
                                              long authorId, int postQuantity) {
        TypedQuery<Post> query = entityManager.createQuery(
                """
                        SELECT p FROM Post p
                        WHERE p.authorId = :authorId
                        ORDER BY p.publishedAt ASC
                        """
                , Post.class);

        query.setParameter("authorId", authorId);
        query.setMaxResults(postQuantity);
        return query.getResultList();
    }
}
