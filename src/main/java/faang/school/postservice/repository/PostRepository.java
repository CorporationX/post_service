package faang.school.postservice.repository;

import faang.school.postservice.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
            WHERE p.authorId = :authorId AND p.published = :published AND p.deleted = :deleted
            """)
    List<Post> findByAuthorIdAndPublishedAndDeletedWithLikes(long authorId, boolean published, boolean deleted);

    @Query("""
            SELECT p FROM Post p
            LEFT JOIN FETCH p.likes
            WHERE p.projectId = :projectId AND p.published = :published AND p.deleted = :deleted
            """)
    List<Post> findByProjectIdAndPublishedAndDeletedWithLikes(long projectId, boolean published, boolean deleted);

    @Query("SELECT p FROM Post p WHERE p.published = false AND p.deleted = false AND p.scheduledAt <= CURRENT_TIMESTAMP")
    List<Post> findReadyToPublish();

    @Query("SELECT p FROM Post p WHERE p.isVerify = 'UNCHECKED'")
    List<Post> findAllUncheckedPosts();

    @Query("SELECT p FROM Post p WHERE p.isVerify = 'NOT_VERIFIED'")
    List<Post> findAllNotVerifiedPosts();

    @Query(nativeQuery = true, value = """
            SELECT p.id FROM post p
            WHERE p.author_id IN :subscriberIds AND p.published = true AND p.deleted = false
            ORDER BY p.published_at DESC
            """)
    List<Long> findFeedPostIdsBySubscriberIds(List<Long> subscriberIds, Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT * FROM post p
            WHERE p.author_id = :authorId AND p.published = true AND p.deleted = false
            ORDER BY p.published_at DESC
            LIMIT :amount
            """)
    List<Post> findFeedPostsByAuthorId(long authorId, int amount);
}
