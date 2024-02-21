package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
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
    List<Post> findByProjectIdWithLikes(@Param("projectId") long projectId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.likes WHERE p.authorId = :authorId")
    List<Post> findByAuthorIdWithLikes(@Param("authorId") long authorId);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.verifiedDate = null OR p.updatedAt >= p.verifiedDate")
    List<Post> findNotVerified();

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.corrected = false")
    List<Post> findNotPublished();

    @Query("SELECT p FROM Post p WHERE p.author.id IN :subscribers")
    List<Post> getFirstsPostsBySubscribers(@Param("subscribers") List<Long> subscribers, int sizeOfPosts);

    @Query("SELECT p FROM Post p " +
            "WHERE p.author.id IN :subscribers " +
            "AND p.id > :point " +
            "ORDER BY p.createdAt ASC")
    List<Post> getPostsBySubscribersFromPoint(@Param("subscribers") List<Long> subscribers,
                                              @Param("sizeOfPosts") int sizeOfPosts,
                                              @Param("point") Long point);

    default List<Long> getMissingPostIds(int missingPostCount, Long postId, EntityManager entityManager) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT id FROM posts WHERE id < :postId ORDER BY id DESC LIMIT :missingPostCount", Long.class);
        query.setParameter("postId", postId);
        query.setParameter("missingPostCount", missingPostCount);
        return query.getResultList();
    }
}
